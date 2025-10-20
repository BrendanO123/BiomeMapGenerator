import java.text.ParseException;
import java.util.*;

class UnexpectedValueError extends Exception{

    public short x;
    public short y;

     public UnexpectedValueError(String message, Throwable err){
        super(message, err);
    }

    public UnexpectedValueError(String message){
        super(message);
    }

    public UnexpectedValueError(){}
}

public class StackFunctions {

 private ArrayList<ArrayList<Short>> valueMap;
 public Random randNum;
 public FastNoiseLite NoiseMap;
 public short width;
 public short length;
 public int max, min;

 public static enum Scale_Type {
    Local,
    Distant,
    Adjacent,
    Global;
 }
 //TODO: Noise Maps Stack

 public static enum Noise_Type {
    WhiteNoise,
    CubicNoise,
    Perlin;
 }

 static byte Type_Local=1;
 static byte Type_Distant=2;
 static byte Type_Adjacent=0;
 static byte Type_Global=3;

 static byte Type_WhiteNoise=0;
 static byte Type_Perlin=1;

    public static final byte[][] MovePatterns = {
                                {-4,-4},{-4,-3},{-4,-2},{-4,-1},{-4,0},{-4,1},{-4,2},{-4,3},{-4,4},
                                {-3,-4},{-3,-3},{-3,-2},{-3,-1},{-3,0},{-3,1},{-3,2},{-3,3},{-3,4},
                                {-2,-4},{-2,-3},{-2,-2},{-2,-1},{-2,0},{-2,1},{-2,2},{-2,3},{-2,4},
                                {-1,-4},{-1,-3},{-1,-2},{-1,-1},{-1,0},{-1,1},{-1,2},{-1,3},{-1,4},
                                {0,-4}, {0,-3}, {0,-2}, {0,-1},        {0,1}, {0,2}, {0,3}, {0,4},
                                {1,-4}, {1,-3}, {1,-2}, {1,-1}, {1,0}, {1,1}, {1,2}, {1,3}, {1,4},
                                {2,-4}, {2,-3}, {2,-2}, {2,-1}, {2,0}, {2,1}, {2,2}, {2,3}, {2,4},
                                {3,-4}, {3,-3}, {3,-2}, {3,-1}, {3,0}, {3,1}, {3,2}, {3,3}, {3,4},
                                {4,-4}, {4,-3}, {4,-2}, {4,-1}, {4,0}, {4,1}, {4,2}, {4,3}, {4,4}};

    public static final byte[][] MovePatternsDistant = {
                                {-4,-4},{-4,-3},{-4,-2},{-4,-1},{-4,0},{-4,1},{-4,2},{-4,3},{-4,4},
                                {-3,-4},{-3,-3},{-3,-2},{-3,-1},{-3,0},{-3,1},{-3,2},{-3,3},{-3,4},
                                {-2,-4},{-2,-3},                                     {-2,3},{-2,4},
                                {-1,-4},{-1,-3},                                     {-1,3},{-1,4},
                                {0,-4}, {0,-3},                                      {0,3}, {0,4},
                                {1,-4}, {1,-3},                                      {1,3}, {1,4},
                                {2,-4}, {2,-3},                                      {2,3}, {2,4},
                                {3,-4}, {3,-3}, {3,-2}, {3,-1}, {3,0}, {3,1}, {3,2}, {3,3}, {3,4},
                                {4,-4}, {4,-3}, {4,-2}, {4,-1}, {4,0}, {4,1}, {4,2}, {4,3}, {4,4}};

    public static final byte[][] MovePatternsNonAdjacent = {
                                {-2,-2},{-2,-1},{-2,0},{-2,1},{-2,2},
                                {-1,-2},                      {-1,2},
                                {0,-2},                       {0,2},
                                {1,-2},                       {1,2},
                                {2,-2}, {2,-1}, {2,0}, {2,1}, {2,2}};

    public static final byte[][] MovePatternsAdjacent = {
                                {-1,-1},{-1,0},{-1,1},
                                {0,-1},        {0,1},
                                {1,-1}, {1,0}, {1,1}};


    public StackFunctions(Random randNum1, FastNoiseLite LandNoise){
        valueMap = new ArrayList<ArrayList<Short>>();
        randNum=randNum1;
        NoiseMap=LandNoise;
    }

    public ArrayList<ArrayList<Short>> getMap(){
        return valueMap;
    }

    public void setMap(ArrayList<ArrayList<Short>> LandMap){
        valueMap=LandMap;
    }

    public void RemoveOutliers (Scale_Type type) throws ParseException{
        if (type==Scale_Type.Adjacent){RemoveOutliersAdjacent();}
        else if (type==Scale_Type.Local){RemoveOutliersLocal();}
        else if (type==Scale_Type.Distant){removeOutliersDistant();}
        else{
            ParseException e = new ParseException("Function: Remove Outliers, Not Available With Given Scale Type", -1);
            throw e;
        }
    }

    public void DivideFalseDeserts(Scale_Type type, int chanceToSpawn) throws ParseException{
        if (type==Scale_Type.Adjacent){divideFalseDesertsAdjacent(chanceToSpawn);}
        else if (type==Scale_Type.Local){divideFalseDesertsLocal(chanceToSpawn);}
        else if (type==Scale_Type.Distant){divideFalseDesertsDistant(1-(1.0/chanceToSpawn));}
        else{
            ParseException e = new ParseException("Function: Divide False Deserts, Not Available With Given Scale Type", -1);
            throw e;
        }
    }

    public void BridgeLand(Scale_Type type, double threshold) throws ParseException{
        if (type==Scale_Type.Distant){bridgeLand(threshold);}
        else {
            throw new ParseException("Function: Divide False Deserts, Not Available With Given Scale Type", -1);
        }
    }

    public void ConnectIslands(Scale_Type type, int chanceToErodeCorners, int chanceToErodeConcave, int chanceToSpreadInternalCorners, int chanceToSpreadExternalCorners, int chanceToSpreadBulge) throws ParseException{
        if (type==Scale_Type.Adjacent){
            connectIslandsAdjacent(chanceToErodeCorners, chanceToErodeConcave, chanceToSpreadInternalCorners, chanceToSpreadExternalCorners, chanceToSpreadBulge);
        }
        else if (type==Scale_Type.Local){
            connectIslandsLocal(chanceToErodeCorners, chanceToErodeConcave, chanceToSpreadInternalCorners, chanceToSpreadExternalCorners, chanceToSpreadBulge);
        }
        else if (type==Scale_Type.Distant){connectIslandsDistant(chanceToErodeCorners, chanceToErodeConcave, chanceToSpreadInternalCorners, chanceToSpreadExternalCorners, chanceToSpreadBulge);}
        else{
            ParseException e = new ParseException("Function: Connect Islands, Not Available With Given Scale Type", -1);
            throw e;
        }
    }

    public void Zoom(Scale_Type type) throws ParseException{
        if (type==Scale_Type.Local || type==Scale_Type.Distant){FastZoom();}
        else{
            ParseException e = new ParseException("Function: Zoom, Not Available With Given Scale Type", -1);
            throw e;
        }
        if (type==Scale_Type.Distant){RefreshThirdRing();}
    }

    public void Smudge(Scale_Type type, Noise_Type NoiseType, double smearStrength) throws ParseException{

        //TODO: Map From Stack Noise Type
        //TODO: Repeat Stack Noise Map to Cover Necessary Size Tag Option

        if (type==Scale_Type.Local){

            if (NoiseType==Noise_Type.WhiteNoise){SmudgeLocalWhite(smearStrength);}
            else if (NoiseType==Noise_Type.Perlin){SmudgeLocalPerlin(smearStrength);}

            else{
                ParseException e = new ParseException("Function: Smudge, Not Available With Given Noise Type", -1);
                throw e;
            }
        }
        else if (type==Scale_Type.Distant){

            if (NoiseType==Noise_Type.WhiteNoise){SmudgeDistantWhite(smearStrength);}
            else if (NoiseType==Noise_Type.Perlin){SmudgeDistantPerlin(smearStrength);}

            else{
                ParseException e = new ParseException("Function: Smudge, Not Available With Given Noise Type", -1);
                throw e;
            }
        }
        else{
            ParseException e = new ParseException("Function: Smudge, Not Available With Given Scale Type", -1);
            throw e;
        }
    }

    public void ZoomBlurred(Scale_Type type, Noise_Type NoiseType, double smearStrength) throws ParseException{
        if (type==Scale_Type.Local){

            if (NoiseType==Noise_Type.WhiteNoise){
                FastZoom();
                SmudgeLocalWhite(smearStrength);
            }
            else if (NoiseType==Noise_Type.Perlin){
                FastZoom();
                SmudgeLocalPerlin(smearStrength);
            }

            else{
                ParseException e = new ParseException("Function: Zoom Blurred, Not Available With Given Noise Type", -1);
                throw e;
            }
        }
        else if (type==Scale_Type.Distant){

            if (NoiseType==Noise_Type.WhiteNoise){
                FastZoom();
                RefreshThirdRing();
                SmudgeDistantWhite(smearStrength);
            }
            else if (NoiseType==Noise_Type.Perlin){
                FastZoom();
                RefreshThirdRing();
                SmudgeDistantPerlin(smearStrength);
            }

            else{
                ParseException e = new ParseException("Function: Zoom Blurred, Not Available With Given Noise Type", -1);
                throw e;
            }
        }
        else{
            ParseException e = new ParseException("Function: Zoom Blurred, Not Available With Given Scale Type", -1);
            throw e;
        }
    }

    public void ConnectingZoom(Scale_Type type, Noise_Type NoiseType, double smearStrength, byte ConnectionType, int chanceToErodeCorners, int chanceToErodeConcave, int chanceToSpreadInternalCorners, int chanceToSpreadExternalCorners, int chanceToSpreadBulge) throws ParseException{
        if (type==Scale_Type.Local){
            if (NoiseType==Noise_Type.WhiteNoise){
                FastZoom();
                SmudgeLocalWhite(smearStrength);
            }
            else if (NoiseType==Noise_Type.Perlin){
                FastZoom();
                SmudgeLocalPerlin(smearStrength);
            }

            else{
                ParseException e = new ParseException("Function: Connecting Zoom, Not Available With Given Noise Type", -1);
                throw e;
            }
        }
        else if (type==Scale_Type.Distant){
            if (NoiseType==Noise_Type.WhiteNoise){
                FastZoom();
                RefreshThirdRing();
                SmudgeDistantWhite(smearStrength);
            }
            else if (NoiseType==Noise_Type.Perlin){
                FastZoom();
                RefreshThirdRing();
                SmudgeDistantPerlin(smearStrength);
            }

            else{
                ParseException e = new ParseException("Function: Connecting Zoom, Not Available With Given Noise Type", -1);
                throw e;
            }
        }
        else{
            ParseException e = new ParseException("Function: Connecting Zoom, Not Available With Given Scale Type", -1);
            throw e;
        }
        if (ConnectionType==0){
            connectIslandsAdjacent(chanceToErodeCorners, chanceToErodeConcave, chanceToSpreadInternalCorners, chanceToSpreadExternalCorners, chanceToSpreadBulge);
        }
        else if(ConnectionType==1){
            connectIslandsLocal(chanceToErodeCorners, chanceToErodeConcave, chanceToSpreadInternalCorners, chanceToSpreadExternalCorners, chanceToSpreadBulge);
        }
        else if(ConnectionType==1){
            connectIslandsDistant(chanceToErodeCorners, chanceToErodeConcave, chanceToSpreadInternalCorners, chanceToSpreadExternalCorners, chanceToSpreadBulge);
        }
        else{
            ParseException e = new ParseException("Function: Connecting Zoom, Not Available With Given Connection Scale Type", -1);
                throw e;
        }
    }



    //TODO: Merge Clean Up Functions
    public short[][] CleanUpUpdated (short VolumeThreshold){

        short[][] returnMap = new short[width][length];
        short x=0;
        short y=0;
        while (x<width){
            try{
                for (x =0;x<width;x++){
                    for (y=0; y<length; y++){
                        //if (x==477 && y==191){System.out.println(returnMap[477][191] + " " + valueMap.get(477).get(191) + " " + ((valueMap.get(477).get(191)&30720)>>11) + " anomaly info");}
                        if (returnMap[x][y]==0){
                            if (valueMap.get(x).get(y)>=0 && ((valueMap.get(x).get(y)&30720)>>11)<8){
                                returnMap=RemoveIslandGlobalUpdated(x, y, VolumeThreshold, returnMap);
                            }
                        }
                        else {
                            y+=(Math.abs(returnMap[x][y])-1);
                            continue;
                        }
                    }
                }
            }
            catch (UnexpectedValueError e){

                //System.out.println("Island Removed");

                /*short x1=e.x;
                short y1=e.y;

                //System.out.println("(" + x1 + ", " + y1 + ") island cords");

                short x2=x1;
                short y2=y1;

                byte increment = (byte)(y1>(length/2) ? -1 : 1);

                    while (true){
                        y2=(short)(y2+increment);
                        if (y2<0 || y2>=length){
                            y2=y1;
                            x2= (short)(x2+ (x1>(width/2) ? -1 : 1));
                        }
                        if ((valueMap.get(x2).get(y2)<0 && ((valueMap.get(x2).get(y2)&30720)>>11)>0)){
                            try {
                                returnMap = resetIsland(x2, y2, returnMap);
                                if (x1<0){throw new UnexpectedValueError();}
                                //returnMap=RemoveIslandGlobalUpdated(x2, y2, VolumeThreshold, returnMap);
                            }
                            catch (UnexpectedValueError e2) {
                                System.out.println("(" + e2.x + ", " + e2.y + "), ");
                                System.err.println("double island remove");
                            }
                            break;
                        }
                    }
                //TODO: remove global islands
                */
            }
        }

        x=0;
        while (x<width){
            try{
                for (x =0;x<width;x++){
                    for (y=0; y<length; y++){
                        if (returnMap[x][y]==0){
                            if (valueMap.get(x).get(y)<0 && ((valueMap.get(x).get(y)&30720)>>11)>0){
                                returnMap=RemoveIslandGlobalUpdated(x, y, VolumeThreshold, returnMap);
                            }
                        }
                        else {
                            y+=(Math.abs(returnMap[x][y])-1);
                            continue;
                        }
                    }
                }
            }
            catch (UnexpectedValueError e){

                short x1=e.x;
                short y1=e.y;

                short x2=x1;
                short y2=y1;

                byte increment = (byte)(y1>(length/2) ? -1 : 1);

                    while (true){
                        y2=(short)(y2+increment);
                        if (y2<0 || y2>=length){
                            y2=y1;
                            x2= (short)(x2+ (x1>(width/2) ? -1 : 1));
                        }
                        if ((valueMap.get(x2).get(y2)>=0 && ((valueMap.get(x2).get(y2)&30720)>>11)<8)){
                            try {
                                returnMap = resetIsland(x2, y2, returnMap);
                                returnMap=RemoveIslandGlobalUpdated(x2, y2, VolumeThreshold, returnMap);
                            }
                            catch (UnexpectedValueError e2) {
                                System.out.println("(" + e2.x + ", " + e2.y + "), ");
                                System.err.println("double island remove");
                            }
                            break;
                        }
                    }
            }
        }

        return returnMap;
    }

    public short[][] resetIsland (short x, short y, short[][] returnMap){
        
        boolean sign = (valueMap.get(x).get(y)>=0);
        returnMap[x][y]=0;
        
        ArrayList<Integer> queue = new ArrayList<Integer>();

            for (byte i=0; i<8; i++){
                short x2 = (short)(x+MovePatternsAdjacent[i][0]);
                short y2 = (short)(y+MovePatternsAdjacent[i][1]);
                if (x2>=0 && y2>=0 && x2<width && y2<length){
                    if ((sign && valueMap.get(x2).get(y2)>=0) || (!sign && valueMap.get(x2).get(y2)<0)){
                        returnMap[x2][y2]=0;
                        queue.add(y2+(x2*length));
                    }
                }
            }

            while (queue.size()>0){

                short x1=(short)(queue.get(0)/length);
                short y1=(short)(queue.get(0)%length);

                queue.remove(0);

                for (byte i=0; i<8; i++){
                    short x2 = (short)(x1+MovePatternsAdjacent[i][0]);
                    short y2 = (short)(y1+MovePatternsAdjacent[i][1]);
                    if (x2>=0 && y2>=0 && x2<width && y2<length){
                        if ((sign && valueMap.get(x2).get(y2)>=0) || (!sign && valueMap.get(x2).get(y2)<0)){
                            if (returnMap[x2][y2]!=0){
                                returnMap[x2][y2]=0;
                                queue.add(y2+(x2*length));
                            }
                        }
                    }
                }
            }
        return returnMap;
    }

    public short[][] RemoveIslandGlobalUpdated(short x, short y, short VolumeThreshold, short[][] returnMap) throws UnexpectedValueError{

        //TODO: get dist map
        byte sign = (byte)(valueMap.get(x).get(y)>=0 ? 1 : -1);
        returnMap[x][y]=sign;
        
        ArrayList<Integer> queue = new ArrayList<Integer>();

        queue.add(y+(x*length));

            for (byte i=0; i<8; i++){
                short x2 = (short)(x+MovePatternsAdjacent[i][0]);
                short y2 = (short)(y+MovePatternsAdjacent[i][1]);
                if (x2>=0 && y2>=0 && x2<width && y2<length){
                    if ((sign ==1 && valueMap.get(x2).get(y2)>=0) || (sign ==-1 && valueMap.get(x2).get(y2)<0)){
                        returnMap[x2][y2]=(short)(2*sign);
                        queue.add(y2+(x2*length));
                    }
                }
            }

            int j=1;
            while (j<queue.size()){

                short x1=(short)(queue.get(j)/length);
                short y1=(short)(queue.get(j)%length);

                if (((valueMap.get(x1).get(y1)&30720)>>11)>0 && ((valueMap.get(x1).get(y1)&30720)>>11)<8){
                    returnMap[x1][y1]=sign;
                }

                short SelfValue=returnMap[x1][y1];

                for (byte i=0; i<8; i++){
                    short x2 = (short)(x1+MovePatternsAdjacent[i][0]);
                    short y2 = (short)(y1+MovePatternsAdjacent[i][1]);
                    if (x2>=0 && y2>=0 && x2<width && y2<length){
                        if ((sign == 1 && valueMap.get(x2).get(y2)>=0) || (sign == -1 && valueMap.get(x2).get(y2)<0)){
                            if (returnMap[x2][y2]==0 || Math.abs(returnMap[x2][y2])>Math.abs(SelfValue+sign)){
                                returnMap[x2][y2]=(short)(SelfValue+sign);
                                queue.add(y2+(x2*length));
                            }
                            else{
                                //System.out.println(returnMap[x2][y2] + " " + sign + " " + SelfValue);
                            }
                        }
                    }
                }
                j++;
            }

        HashSet<Integer> removeDuplex = new HashSet<Integer>();
        removeDuplex.addAll(queue);
        queue= new ArrayList<Integer>();
        queue.addAll(removeDuplex);
        removeDuplex=null;

        if (queue.size()<VolumeThreshold){
            for (int i=0; i<queue.size();i++){

                short x1=(short)(queue.get(i)/length);
                short y1=(short)(queue.get(i)%length);

                returnMap[x1][y1]=0;

                if (sign==1){
                    valueMap.get(x1).set(y1, (short)(valueMap.get(x1).get(y1)+(short)32768));
                    for (j=0; j<8; j++){
                        short x2=(short)(x1+MovePatternsAdjacent[j][0]);
                        if (x2>=0 && x2<width){
                            short y2=(short)(y1+MovePatternsAdjacent[j][1]);
                            if (y2>=0 && y2<length){
                                valueMap.get(x2).set(y2, (short)(valueMap.get(x2).get(y2)-2048));
                            }
                        }
                    }
                }
                else{
                    valueMap.get(x1).set(y1, (short)(valueMap.get(x1).get(y1)&32767));
                    for (j=0; j<8; j++){
                        short x2=(short)(x1+MovePatternsAdjacent[j][0]);
                        if (x2>=0 && x2<width){
                            short y2=(short)(y1+MovePatternsAdjacent[j][1]);
                            if (y2>=0 && y2<length){
                                valueMap.get(x2).set(y2, (short)(valueMap.get(x2).get(y2)+2048));
                            }
                        }
                    }
                }
            }
            UnexpectedValueError e = new UnexpectedValueError();
            e.x=x;
            e.y=y;
            throw e;
        }

        return returnMap;
    }


    public int[][] CleanUp(short VolumeThreshold){

        int[][] returnMap= new int[width][length];
        
        for (short x=0; x<width;x++){
            for (short y=0; y<length; y++){
                if (((valueMap.get(x).get(y)&30720)>>11)>0 && ((valueMap.get(x).get(y)&30720)>>11)<8){
                    if (returnMap[x][y]==0){
                    returnMap = RemoveIslandGlobal(x, y, VolumeThreshold, returnMap);
                    }
                }
            }
        }

        LinkedHashSet<Integer> Locations = new LinkedHashSet<Integer>();

        for (short x=0; x<width;x++){
            for (short y=0; y<length; y++){
                if (returnMap[x][y]==0){
                    if (((valueMap.get(x).get(y)&30720)>>11)>0 && ((valueMap.get(x).get(y)&30720)>>11)<8){
                        System.err.println("Extra edge detected late in Clean Up Function " + ((valueMap.get(x).get(y)&30720)>>11) + " (" + x + ", " + y + ")");
                    }
                    else{
                        Locations.add(y+(x*length));
                    }
                }
            }
        }
        while (Locations.size()!=0){
            int element = (Integer)Locations.toArray()[0];
            short x =(short)(element/length);
            short y =(short)(element%length);
            int value=0;
            for (byte i=0; i<8; i++){
                short x2=(short)(x+MovePatternsAdjacent[i][0]);
                if (x2>=0 && x2<width){
                    short y2 = (short)(y+MovePatternsAdjacent[i][1]);
                    if (y2>=0 && y2<length){
                        if (returnMap[x2][y2]!=0){
                            value = (value>=0 ? 1 : -1)*(Math.max(Math.abs(value), Math.abs(returnMap[x2][y2])));
                        }
                    }
                }
            }
            if (value==0){
                Locations.remove(element);
                Locations.add(element);
                continue;
            }
            else{
                returnMap[x][y]=((value>=0 ? 1 : -1)*(Math.abs(value)+1));
                Locations.remove(element);
            }
        }
        return returnMap;
    }

    private int[][] RemoveIslandGlobal(short x, short y, short VolumeThreshold, int[][] returnMap){
        
        LinkedHashSet<Integer> indexQue = new LinkedHashSet<Integer>();
        ArrayList<Short> Locations = new ArrayList<Short>();
        
        byte sign= (byte)(valueMap.get(x).get(y)>=0 ? 1 : -1);

        indexQue.add((y+(x*length)));
        Locations.add(x);
        Locations.add(y);

        returnMap[x][y]=sign;

        int repetitions=0;
        while (indexQue.size()!=0){
            int element=(int)(Integer)indexQue.toArray()[0];
            short x1 =(short)(element/length);
            short y1 =(short)(element%length);
            int SelfValue = returnMap[x1][y1];
            for (byte j=(byte)0;j<8;j++){
                short x2=(short)(x1+MovePatternsAdjacent[j][0]);
                short y2=(short)(y1+MovePatternsAdjacent[j][1]);
                if (x2>=0 && y2>=0 && x2<width && y2<length){
                    if (((valueMap.get(x2).get(y2)&30720)>>11)>0 && ((valueMap.get(x2).get(y2)&30720)>>11)<8){
                        if (returnMap[x2][y2]!=sign && (sign ==1 ? valueMap.get(x2).get(y2)>=0: valueMap.get(x2).get(y2)<0)){
                            returnMap[x2][y2]=sign;
                            indexQue.add(y2+(x2*length));
                            Locations.add(x2);
                            Locations.add(y2);
                        }
                    }
                    else if (sign==1){
                        if (valueMap.get(x2).get(y2)>=0){
                            if (returnMap[x2][y2]>(SelfValue+sign) || returnMap[x2][y2]==0){
                                returnMap[x2][y2]=SelfValue+sign;
                                indexQue.add(y2+(x2*length));
                            }
                            if (returnMap[x2][y2]==0){
                                Locations.add(x2);
                                Locations.add(y2);
                            }
                        }
                    }
                    else{
                        if (valueMap.get(x2).get(y2)<0){
                            if (returnMap[x2][y2]<(SelfValue+sign) || returnMap[x2][y2]==0){
                                returnMap[x2][y2]=SelfValue+sign;
                                indexQue.add(y2+(x2*length));
                            }
                            if (returnMap[x2][y2]==0){
                                Locations.add(x2);
                                Locations.add(y2);
                            }
                        }
                    }
                }
            }
            repetitions++;
            indexQue.remove(element);
        }
        indexQue=null;

        if (repetitions<VolumeThreshold){
            for (int i=0; i<Locations.size();i+=2){
                short x1=Locations.get(i);
                short y1 = Locations.get(i+1);
                if (sign==1){
                    valueMap.get(x1).set(y1, (short)(valueMap.get(x1).get(y1)+(short)32768));
                    for (short j=0; j<8; j++){
                        short x2=(short)(x1+MovePatternsAdjacent[j][0]);
                        short y2=(short)(y1+MovePatternsAdjacent[j][1]);
                        if (x2>=0 && y2>=0 && y2<length && x2<width){
                            valueMap.get(x2).set(y2, (short)(valueMap.get(x2).get(y2)-2048));
                        }
                    }
                }
                else{
                    valueMap.get(x1).set(y1, (short)(valueMap.get(x1).get(y1)&32767));
                    for (short j=0; j<8; j++){
                        short x2=(short)(x1+MovePatternsAdjacent[j][0]);
                        short y2=(short)(y1+MovePatternsAdjacent[j][1]);
                        if (x2>=0 && y2>=0 && y2<length && x2<width){
                            valueMap.get(x2).set(y2, (short)(valueMap.get(x2).get(y2)+2048));
                        }
                    }
                }
            }
        }
        Locations=null;
        return returnMap;
    }

    public void RemoveIslandLocal(short x, short y){
        
        short x2;
        short y2;
        
        ArrayList<Short> alteredIndexes = new ArrayList<Short>();

        valueMap.get(x).set(y,(short)(valueMap.get(x).get(y)+(short)32768));
        alteredIndexes.add(x);
        alteredIndexes.add(y);

        for (int i=0;i<8;i++){
            x2=(short)(x+MovePatternsAdjacent[i][0]);
            y2=(short)(y+MovePatternsAdjacent[i][1]);
            if (x2>=0 && y2>=0 && x2<width && y2<length){
                if (valueMap.get(x2).get(y2)>=0){
                    valueMap.get(x).set(y,(short)(valueMap.get(x).get(y)+(short)32768));
                    alteredIndexes.add(x);
                    alteredIndexes.add(y);
                }
            }
        }
        for (int i=0;i<16;i++){
           x2=(short)(x+MovePatternsNonAdjacent[i][0]);
            y2=(short)(y+MovePatternsNonAdjacent[i][1]);
            if (x2>=0 && y2>=0 && x2<width && y2<length){
                if (valueMap.get(x2).get(y2)>=0){
                    valueMap.get(x).set(y,(short)(valueMap.get(x).get(y)+(short)32768));
                    alteredIndexes.add(x);
                    alteredIndexes.add(y);
                }
            }
        } 



        for (int a=0;a<alteredIndexes.size();a+=2){
            short xi=alteredIndexes.get(a);
            short yi=alteredIndexes.get(a+1);
            byte sign =(byte)(valueMap.get(xi).get(yi)>=0 ? 1 : -1);
             for (int i=0;i<8;i++){
                x2=(short)(xi+MovePatternsAdjacent[i][0]);
                y2=(short)(yi+MovePatternsAdjacent[i][1]);
                if (x2>=0 && y2>=0 && x2<width && y2<length){
                    valueMap.get(x2).set(y2, (short)(valueMap.get(x2).get(y2)+(2048*sign)));
                }
            }
            for (int i=0;i<16;i++){
                x2=(short)(xi+MovePatternsNonAdjacent[i][0]);
                y2=(short)(yi+MovePatternsNonAdjacent[i][1]);
                if (x2>=0 && y2>=0 && x2<width && y2<length){
                    valueMap.get(x2).set(y2, (short)(valueMap.get(x2).get(y2)+(64*sign)));
                }
            } 
            for (int i=0;i<56;i++){
                x2=(short)(xi+MovePatternsDistant[i][0]);
                y2=(short)(yi+MovePatternsDistant[i][1]);
                if (x2>=0 && y2>=0 && x2<width && y2<length){
                    valueMap.get(x2).set(y2, (short)(valueMap.get(x2).get(y2)+sign));
                }
            } 
        }
        alteredIndexes=null;
    }

    private void removeOutliersDistant(){

        short x2;
        short y2;
        short value;
        byte sign;
        
        ArrayList<Short> alteredIndexes = new ArrayList<Short>();

        for (short x=0; x<width; x++){
            iteration : for (short y=0; y<length; y++){
                value=valueMap.get(x).get(y);
                sign =(byte)(value>=0 ? 1 : -1);
                if (sign==1){
                    if (((value&30720)>>11)==0){
                        valueMap.get(x).set(y, (short)(value+(short)32768));
                        alteredIndexes.add(x);
                        alteredIndexes.add(y);
                        continue iteration;
                    }
                    else if (((value&1984)>>6)==0){
                        valueMap.get(x).set(y, (short)(value+(short)32768));
                        alteredIndexes.add(x);
                        alteredIndexes.add(y);
                        continue iteration;
                    }
                    else if ((value&63)==0){
                        valueMap.get(x).set(y, (short)(value+(short)32768));
                        alteredIndexes.add(x);
                        alteredIndexes.add(y);
                        for (byte i=(byte)0; i<8;i++){
                            x2=(short)(x+MovePatternsAdjacent[i][0]);
                            y2=(short)(y+MovePatternsAdjacent[i][1]);
                            if (x2>=0 && y2>=0 && x2<width && y2<length && valueMap.get(x2).get(y2)>=0){
                                valueMap.get(x2).set(y2, (short)(valueMap.get(x2).get(y2)+(short)32768));
                                alteredIndexes.add(x2);
                                alteredIndexes.add(y2);
                            }
                        }
                        for (byte i=(byte)0; i<16;i++){
                            x2=(short)(x+MovePatternsNonAdjacent[i][0]);
                            y2=(short)(y+MovePatternsNonAdjacent[i][1]);
                            if (x2>=0 && y2>=0 && x2<width && y2<length && valueMap.get(x2).get(y2)>=0){
                                valueMap.get(x2).set(y2, (short)(valueMap.get(x2).get(y2)+(short)32768));
                                alteredIndexes.add(x2);
                                alteredIndexes.add(y2);
                            }
                        }
                        continue iteration;
                    }
                    /*else if (((value&1984)>>6)>(value&63)){
                        valueMap.get(x).set(y, (short)(value+(short)32768));
                        alteredIndexes.add(x);
                        alteredIndexes.add(y);
                        continue iteration;
                    }*/
                }
                else{
                    if (((value&30720)>>11)==8){
                        valueMap.get(x).set(y, (short)(value&32767));
                        alteredIndexes.add(x);
                        alteredIndexes.add(y);
                        continue iteration;
                    }
                    else if (((value&1984)>>6)==16){
                        valueMap.get(x).set(y, (short)(value&32767));
                        alteredIndexes.add(x);
                        alteredIndexes.add(y);
                        continue iteration;
                    }
                    else if ((value&63)==56){
                        valueMap.get(x).set(y, (short)(value&32767));
                        alteredIndexes.add(x);
                        alteredIndexes.add(y);
                        for (byte i=(byte)0; i<8;i++){
                            x2=(short)(x+MovePatternsAdjacent[i][0]);
                            y2=(short)(y+MovePatternsAdjacent[i][1]);
                            if (x2>=0 && y2>=0 && x2<width && y2<length && valueMap.get(x2).get(y2)<0){
                                valueMap.get(x2).set(y2, (short)(valueMap.get(x2).get(y2)&32767));
                                alteredIndexes.add(x2);
                                alteredIndexes.add(y2);
                            }
                        }
                        for (byte i=(byte)0; i<16;i++){
                            x2=(short)(x+MovePatternsNonAdjacent[i][0]);
                            y2=(short)(y+MovePatternsNonAdjacent[i][1]);
                            if (x2>=0 && y2>=0 && x2<width && y2<length && valueMap.get(x2).get(y2)<0){
                                valueMap.get(x2).set(y2, (short)(valueMap.get(x2).get(y2)&32767));
                                alteredIndexes.add(x2);
                                alteredIndexes.add(y2);
                            }
                        }
                        continue iteration;
                    }
                }
            }
        }
        for (int i=0; i<alteredIndexes.size();i+=2){
            short x =alteredIndexes.get(i);
            short y =alteredIndexes.get(i+1);
            sign = (byte)((valueMap.get(x).get(y)>=0) ? 1 : -1);
            for (int a=0; a<8;a++){
                x2=(short)(x+StackFunctions.MovePatternsAdjacent[a][0]);
                y2=(short)(y+StackFunctions.MovePatternsAdjacent[a][1]);
                if (x2>=0 && y2>=0 && x2<width && y2<length){
                    valueMap.get(x2).set(y2, (short)(valueMap.get(x2).get(y2)+(2048*sign)));
                }
            }
            for (int a=0; a<16;a++){
                x2=(short)(x+StackFunctions.MovePatternsNonAdjacent[a][0]);
                y2=(short)(y+StackFunctions.MovePatternsNonAdjacent[a][1]);
                if (x2>=0 && y2>=0 && x2<width && y2<length){
                    valueMap.get(x2).set(y2, (short)(valueMap.get(x2).get(y2)+(64*sign)));
                }
            }
            for (int a=0;a<56;a++){
                x2 =(short)(x + MovePatternsDistant[a][0]);
                y2 =(short)(y + MovePatternsDistant[a][1]);
                if (x2>=0 && y2>=0 && x2<width && y2<length){
                    valueMap.get(x2).set(y2, (short)(valueMap.get(x2).get(y2)+sign));
                }
            }
        }
    }



    private void RemoveOutliersLocal(){

        short x2;
        short y2;
        short value;
        byte sign;
        
        ArrayList<Short> alteredIndexes = new ArrayList<Short>();

        for (short x=0; x<width; x++){
            iteration : for (short y=0; y<length; y++){
                value=valueMap.get(x).get(y);
                sign =(byte)(value>=0 ? 1 : -1);
                if (sign==1){
                    if (((value&30720)>>11)==0 || ((value&1984)>>6)==0){
                        valueMap.get(x).set(y, (short)(value+(short)32768));
                        alteredIndexes.add(x);
                        alteredIndexes.add(y);
                        continue iteration;
                    }
                }
                else{
                    if (((value&30720)>>11)==8 || ((value&1984)>>6)==16){
                        valueMap.get(x).set(y, (short)(value&32767));
                        alteredIndexes.add(x);
                        alteredIndexes.add(y);
                        continue iteration;
                    }
                }
            }
        }

        for (int i=0; i<alteredIndexes.size();i+=2){
            short x =alteredIndexes.get(i);
            short y =alteredIndexes.get(i+1);
            sign = (byte)((valueMap.get(x).get(y)>=0) ? 1 : -1);
            for (int a=0; a<8;a++){
                x2=(short)(x+StackFunctions.MovePatternsAdjacent[a][0]);
                y2=(short)(y+StackFunctions.MovePatternsAdjacent[a][1]);
                if (x2>=0 && y2>=0 && x2<width && y2<length){
                    valueMap.get(x2).set(y2, (short)(valueMap.get(x2).get(y2) + 2048*sign));
                }
            }
            for (int a=0; a<16;a++){
                x2=(short)(x+StackFunctions.MovePatternsNonAdjacent[a][0]);
                y2=(short)(y+StackFunctions.MovePatternsNonAdjacent[a][1]);
                if (x2>=0 && y2>=0 && x2<width && y2<length){
                    valueMap.get(x2).set(y2, (short)(valueMap.get(x2).get(y2) + 64*sign));
                }
            }
        }
    }


    private void RemoveOutliersAdjacent(){
        short x2;
        short y2;
        short value;
        byte sign;
        
        ArrayList<Short> alteredIndexes = new ArrayList<Short>();

        for (short x=0; x<width; x++){
            iteration : for (short y=0; y<length; y++){
                value=valueMap.get(x).get(y);
                sign =(byte)(value>=0 ? 1 : -1);
                if (sign==1){
                    if (((value&30720)>>11)==0){
                        valueMap.get(x).set(y, (short)(value+(short)32768));
                        alteredIndexes.add(x);
                        alteredIndexes.add(y);
                        continue iteration;
                    }
                }
                else{
                    if (((value&30720)>>11)==8){
                        valueMap.get(x).set(y, (short)(value&32767));
                        alteredIndexes.add(x);
                        alteredIndexes.add(y);
                        continue iteration;
                    }
                }
            }
        }

        for (int i=0; i<alteredIndexes.size();i+=2){
            short x =alteredIndexes.get(i);
            short y =alteredIndexes.get(i+1);
            sign = (byte)((valueMap.get(x).get(y)>=0) ? 1 : -1);
            for (int a=0; a<8;a++){
                x2=(short)(x+StackFunctions.MovePatternsAdjacent[a][0]);
                y2=(short)(y+StackFunctions.MovePatternsAdjacent[a][1]);
                if (x2>=0 && y2>=0 && x2<width && y2<length){
                    valueMap.get(x2).set(y2, (short)(2048*sign + valueMap.get(x2).get(y2)));
                }
            }
            for (int a=0; a<16;a++){
                x2=(short)(x+StackFunctions.MovePatternsNonAdjacent[a][0]);
                y2=(short)(y+StackFunctions.MovePatternsNonAdjacent[a][1]);
                if (x2>=0 && y2>=0 && x2<width && y2<length){
                    valueMap.get(x2).set(y2, (short)(64*sign + valueMap.get(x2).get(y2)));
                }
            }
        }
    }





    private void connectIslandsDistant(int chanceToErodeCorners, int chanceToErodeConcave, int chanceToSpreadInternalCorners, int chanceToSpreadExternalCorners, int chanceToSpreadBulge){

        ArrayList<Short> alteredIndexes = new ArrayList<Short>();

        for (short x=0; x<width; x++){
            iteration : for (short y=0; y<length; y++){
                short value=valueMap.get(x).get(y);
                byte sign = (byte)(value>=0 ? 1 : -1);
                if (sign==1){
                    if (((value&30720)>>11)==3 && ((value&1984)>>6)>=5 && (value&63)>=14 && randNum.nextInt(chanceToErodeCorners-1)==0){
                        valueMap.get(x).set(y, (short)(value+(short)32768));
                        alteredIndexes.add(x);
                        alteredIndexes.add(y);
                        continue iteration;
                    }
                    else if (((value&30720)>>11)==5 && ((value&1984)>>6)>=9 &&  (value&63)>=23 && randNum.nextInt(chanceToErodeConcave-1)==0){
                        valueMap.get(x).set(y, (short)(value+(short)32768));
                        alteredIndexes.add(x);
                        alteredIndexes.add(y);
                        continue iteration;
                    }
                }
                else{
                    if (((value&30720)>>11)==5 && ((value&1984)>>6)>=9 && (value&63)>=25 && randNum.nextInt(chanceToSpreadInternalCorners-1)==0){
                        valueMap.get(x).set(y, (short)(value&32767));
                        alteredIndexes.add(x);
                        alteredIndexes.add(y);
                        continue iteration;
                    }
                    else if (((value&30720)>>11)==1 && ((value&1984)>>6)>=5 && (value&63)>=12 && randNum.nextInt(chanceToSpreadExternalCorners-1)==0){
                        valueMap.get(x).set(y, (short)(value&32767));
                        alteredIndexes.add(x);
                        alteredIndexes.add(y);
                        continue iteration;
                    }
                    else if (((value&30720)>>11)==3 &&((value&1984)>>6)>=7 && (value&63)>=19 && randNum.nextInt(chanceToSpreadBulge-1)==0){
                        valueMap.get(x).set(y, (short)(value&32767));
                        alteredIndexes.add(x);
                        alteredIndexes.add(y);
                        continue iteration;
                    }
                }
            }
        }
        for (int a=0; a<alteredIndexes.size();a+=2){
            short x=alteredIndexes.get(a);
            short y=alteredIndexes.get(a+1);
            byte sign = (byte)(valueMap.get(x).get(y)>=0 ? 1 : -1);
            for (int b=0; b<8; b++){
                short x2=(short)(x+MovePatternsAdjacent[b][0]);
                short y2=(short)(y+MovePatternsAdjacent[b][1]);
                if (x2>=0 && y2>=0 && x2<width && y2<length){
                    valueMap.get(x2).set(y2, (short)(valueMap.get(x2).get(y2)+2048*sign));
                }
            }
            for (int b=0; b<16; b++){
                short x2=(short)(x+MovePatternsNonAdjacent[b][0]);
                short y2=(short)(y+MovePatternsNonAdjacent[b][1]);
                if (x2>=0 && y2>=0 && x2<width && y2<length){
                    valueMap.get(x2).set(y2, (short)(valueMap.get(x2).get(y2)+64*sign));
                }
            }
            for (int b=0;b<56;b++){
                short x2=(short)(x+MovePatternsDistant[b][0]);
                short y2=(short)(y+MovePatternsDistant[b][1]);
                if (x2>=0 && y2>=0 && x2<width && y2<length){
                    valueMap.get(x2).set(y2, (short)(valueMap.get(x2).get(y2)+sign));
                }
            }
        }
        alteredIndexes=null;
    }




    private void connectIslandsLocal(int chanceToErodeCorners, int chanceToErodeConcave, int chanceToSpreadInternalCorners, int chanceToSpreadExternalCorners, int chanceToSpreadBulge){
        
        ArrayList<Short> alteredIndexes = new ArrayList<Short>();

        for (short x=0; x<width; x++){
            iteration : for (short y=0; y<length; y++){
                short value=valueMap.get(x).get(y);
                byte sign = (byte)(value>=0 ? 1 : -1);
                if (sign==1){
                    if (((value&30720)>>11)==3 && ((value&1984)>>6)>=5 && randNum.nextInt(chanceToErodeCorners-1)==0){
                        valueMap.get(x).set(y, (short)(value+(short)32768));
                        alteredIndexes.add(x);
                        alteredIndexes.add(y);
                        continue iteration;
                    }
                    else if (((value&30720)>>11)==5 && ((value&1984)>>6)>=9 && randNum.nextInt(chanceToErodeConcave-1)==0){
                        valueMap.get(x).set(y, (short)(value+(short)32768));
                        alteredIndexes.add(x);
                        alteredIndexes.add(y);
                        continue iteration;
                    }
                }
                else{
                    if (((value&30720)>>11)==5 && ((value&1984)>>6)>=9 && randNum.nextInt(chanceToSpreadInternalCorners-1)==0){
                        valueMap.get(x).set(y, (short)(value&32767));
                        alteredIndexes.add(x);
                        alteredIndexes.add(y);
                        continue iteration;
                    }
                    else if (((value&30720)>>11)==1 && ((value&1984)>>6)>=5 && randNum.nextInt(chanceToSpreadExternalCorners-1)==0){
                        valueMap.get(x).set(y, (short)(value&32767));
                        alteredIndexes.add(x);
                        alteredIndexes.add(y);
                        continue iteration;
                    }
                    else if (((value&30720)>>11)==3 &&((value&1984)>>6)>=7 && randNum.nextInt(chanceToSpreadBulge-1)==0){
                        valueMap.get(x).set(y, (short)(value&32767));
                        alteredIndexes.add(x);
                        alteredIndexes.add(y);
                        continue iteration;
                    }
                }
            }
        }
        for (int a=0; a<alteredIndexes.size();a+=2){
            short x=alteredIndexes.get(a);
            short y=alteredIndexes.get(a+1);
            byte sign = (byte)(valueMap.get(x).get(y)>=0 ? 1 : -1);
            for (int b=0; b<8; b++){
                short x2=(short)(x+MovePatternsAdjacent[b][0]);
                short y2=(short)(y+MovePatternsAdjacent[b][1]);
                if (x2>=0 && y2>=0 && x2<width && y2<length){
                    valueMap.get(x2).set(y2, (short)(valueMap.get(x2).get(y2)+2048*sign));
                }
            }
            for (int b=0; b<16; b++){
                short x2=(short)(x+MovePatternsNonAdjacent[b][0]);
                short y2=(short)(y+MovePatternsNonAdjacent[b][1]);
                if (x2>=0 && y2>=0 && x2<width && y2<length){
                    valueMap.get(x2).set(y2, (short)(valueMap.get(x2).get(y2)+64*sign));
                }
            }
        }
        alteredIndexes=null;
    }


    //doesn't refresh third ring
    private void connectIslandsAdjacent(int chanceToErodeCorners, int chanceToErodeConcave, int chanceToSpreadInternalCorners, int chanceToSpreadTumor, int chanceToSpreadExternalCorners){

        ArrayList<Short> alteredIndexes = new ArrayList<Short>();

        for (short x=0; x<width; x++){
            iteration : for (short y=0; y<length; y++){
                short value=valueMap.get(x).get(y);
                byte sign = (byte)(value>=0 ? 1 : -1);
                if (sign==1){
                    if (((value&30720)>>11)==3 && randNum.nextInt(chanceToErodeCorners-1)==0){
                        valueMap.get(x).set(y, (short)(value+(short)32768));
                        alteredIndexes.add(x);
                        alteredIndexes.add(y);
                        continue iteration;
                    }
                    else if (((value&30720)>>11)==5&& randNum.nextInt(chanceToErodeConcave-1)==0){
                        valueMap.get(x).set(y, (short)(value+(short)32768));
                        alteredIndexes.add(x);
                        alteredIndexes.add(y);
                        continue iteration;
                    }
                }
                else{
                    if (((value&30720)>>11)==5 && randNum.nextInt(chanceToSpreadInternalCorners-1)==0){
                        valueMap.get(x).set(y, (short)(value&32767));
                        alteredIndexes.add(x);
                        alteredIndexes.add(y);
                        continue iteration;
                    }
                    else if (((value&30720)>>11)==7 && randNum.nextInt(chanceToSpreadExternalCorners-1)==0){
                        valueMap.get(x).set(y, (short)(value&32767));
                        alteredIndexes.add(x);
                        alteredIndexes.add(y);
                        continue iteration;
                    }
                    else if (((value&30720)>>11)==3&& randNum.nextInt(chanceToSpreadTumor-1)==0){
                        valueMap.get(x).set(y, (short)(value&32767));
                        alteredIndexes.add(x);
                        alteredIndexes.add(y);
                        continue iteration;
                    }
                }
            }
        }
        for (int a=0; a<alteredIndexes.size();a+=2){
            short x=alteredIndexes.get(a);
            short y=alteredIndexes.get(a+1);
            byte sign = (byte)(valueMap.get(x).get(y)>=0 ? 1 : -1);
            for (int b=0; b<8; b++){
                short x2=(short)(x+MovePatternsAdjacent[b][0]);
                short y2=(short)(y+MovePatternsAdjacent[b][1]);
                if (x2>=0 && y2>=0 && x2<width && y2<length){
                    valueMap.get(x2).set(y2, (short)(valueMap.get(x2).get(y2)+2048*sign));
                }
            }
            for (int b=0; b<16; b++){
                short x2=(short)(x+MovePatternsNonAdjacent[b][0]);
                short y2=(short)(y+MovePatternsNonAdjacent[b][1]);
                if (x2>=0 && y2>=0 && x2<width && y2<length){
                    valueMap.get(x2).set(y2, (short)(valueMap.get(x2).get(y2)+64*sign));
                }
            }
        }
        alteredIndexes=null;
    }



    public ArrayList<Integer> refreshInternalDebug(){

        ArrayList<Integer> IncorrectIndexes = new ArrayList<Integer>();

        for (int x =0; x<width; x++){
            for (int y=0; y<length; y++){
                short count=0;
                for (int i =0; i<8; i++){
                    short x2= (short)(x+MovePatternsAdjacent[i][0]);
                    short y2= (short)(y+MovePatternsAdjacent[i][1]);
                    if (x2>=0 && y2>=0 && x2<width && y2<length){
                        if (valueMap.get(x2).get(y2)>=0){count+=32;}
                    }
                }
                for (int i =0; i<16; i++){
                    short x2= (short)(x+MovePatternsNonAdjacent[i][0]);
                    short y2= (short)(y+MovePatternsNonAdjacent[i][1]);
                    if (x2>=0 && y2>=0 && x2<width && y2<length){
                        if (valueMap.get(x2).get(y2)>=0){count++;}
                    }
                }
                if (count!=(((valueMap.get(x).get(y)&30720)>>6)+((valueMap.get(x).get(y)&1984)>>6))){
                    //System.out.println(count + " " + (((valueMap.get(x).get(y)&30720)>>6)+((valueMap.get(x).get(y)&1984)>>6)) + " (" + x + ", " + y + ") ");
                    valueMap.get(x).set(y, (short)((valueMap.get(x).get(y)&63)+(count<<6)+(valueMap.get(x).get(y)&32768)));
                    IncorrectIndexes.add(x);
                    IncorrectIndexes.add(y);
                }
            }
        }
        return IncorrectIndexes;
    }

    //doesn't refresh third ring
    private void SmudgeLocalPerlin (double smearStrength){

        NoiseMap.SetSeed(randNum.nextInt());
        NoiseMap.SetFractalOctaves(1);

        ArrayList<Short> alteredIndexes = new ArrayList<Short>();

        for (int x=0; x<width;x++){
            for (int y=0; y<length; y++){
                short value = valueMap.get(x).get(y);
                byte sign = (value>=0 ? (byte)1 : (byte)-1);
                double ChangingValue=(sign ==1 ? (short)(((value&30720)>>11)+((value&1984)>>6)) : (short)(24-(((value&30720)>>11)+((value&1984)>>6))));
                if (ChangingValue>=8){
                    ChangingValue=(sign*((-0.0001553199404758*(ChangingValue*ChangingValue*ChangingValue))+(0.00750930059522*(ChangingValue*ChangingValue))-(0.00325892857126*(ChangingValue))-(0.00000000000012)));
                }
                else{
                    ChangingValue=(sign*((0.00403571428572*(ChangingValue*ChangingValue))-(0.0010357142858*(ChangingValue))+(0.12500000000003)));
                }
                ChangingValue=(ChangingValue*(1-smearStrength))+(((NoiseMap.GetNoise((float)((x+5)), (float)((y+19)))))*smearStrength*2);
                if (sign==1){
                    if (ChangingValue<0){
                        valueMap.get(x).set(y, (short)(32768+value));
                        alteredIndexes.add((short)x);
                        alteredIndexes.add((short)y);
                    }
                }
                else{
                    if (ChangingValue>=0){
                        valueMap.get(x).set(y, (short)(32768+value));
                        alteredIndexes.add((short)x);
                        alteredIndexes.add((short)y);
                    }
                }
            }
        }

        short x;
        short x2;
        short y;
        short y2;
        int incrementValue;

        for (int a=0;a<alteredIndexes.size();a+=2){
            x = alteredIndexes.get(a);
            y = alteredIndexes.get(a+1);
            incrementValue=(valueMap.get(x).get(y)>=0 ? 1: -1);
            for (int b=0; b<8; b++){
                x2=(short)(x+MovePatternsAdjacent[b][0]);
                y2=(short)(y+MovePatternsAdjacent[b][1]);
                if (x2>=0 && y2>=0 && x2<width && y2<length){
                    valueMap.get(x2).set(y2, (short)(valueMap.get(x2).get(y2)+(2048*incrementValue)));
                }
            }
            for (int b=0; b<16; b++){
                x2=(short)(x+MovePatternsNonAdjacent[b][0]);
                y2=(short)(y+MovePatternsNonAdjacent[b][1]);
                if (x2>=0 && y2>=0 && x2<width && y2<length){
                    valueMap.get(x2).set(y2, (short)(valueMap.get(x2).get(y2)+(64*incrementValue)));
                }
            }
        }
        alteredIndexes=null;
        NoiseMap.SetSeed(randNum.nextInt());
    }
    


    private void SmudgeLocalWhite (double smearStrength){

        NoiseMap.SetSeed(randNum.nextInt());
        NoiseMap.SetFractalOctaves(1);

        ArrayList<Short> alteredIndexes = new ArrayList<Short>();

        for (int x=0; x<width;x++){
            for (int y=0; y<length; y++){
                short value = valueMap.get(x).get(y);
                byte sign = (value>=0 ? (byte)1 : (byte)-1);
                double ChangingValue=(sign ==1 ? (short)(((value&30720)>>11)+((value&1984)>>6)) : (short)(24-(((value&30720)>>11)+((value&1984)>>6))));
                if (ChangingValue>=8){
                    ChangingValue=(sign*((-0.0001553199404758*(ChangingValue*ChangingValue*ChangingValue))+(0.00750930059522*(ChangingValue*ChangingValue))-(0.00325892857126*(ChangingValue))-(0.00000000000012)));
                }
                else{
                    ChangingValue=(sign*((0.00403571428572*(ChangingValue*ChangingValue))-(0.0010357142858*(ChangingValue))+(0.12500000000003)));
                }
                ChangingValue=(ChangingValue*(1-smearStrength))+((randNum.nextDouble(-1.0,1.))*smearStrength*2);
                if (sign==1){
                    if (ChangingValue<0){
                        valueMap.get(x).set(y, (short)(32768+value));
                        alteredIndexes.add((short)x);
                        alteredIndexes.add((short)y);
                    }
                }
                else{
                    if (ChangingValue>=0){
                        valueMap.get(x).set(y, (short)(32768+value));
                        alteredIndexes.add((short)x);
                        alteredIndexes.add((short)y);
                    }
                }
            }
        }

        short x;
        short x2;
        short y;
        short y2;
        int incrementValue;

        for (int a=0;a<alteredIndexes.size();a+=2){
            x = alteredIndexes.get(a);
            y = alteredIndexes.get(a+1);
            incrementValue=(valueMap.get(x).get(y)>=0 ? 1: -1);
            for (int b=0; b<8; b++){
                x2=(short)(x+MovePatternsAdjacent[b][0]);
                y2=(short)(y+MovePatternsAdjacent[b][1]);
                if (x2>=0 && y2>=0 && x2<width && y2<length){
                    valueMap.get(x2).set(y2, (short)(valueMap.get(x2).get(y2)+(2048*incrementValue)));
                }
            }
            for (int b=0; b<16; b++){
                x2=(short)(x+MovePatternsNonAdjacent[b][0]);
                y2=(short)(y+MovePatternsNonAdjacent[b][1]);
                if (x2>=0 && y2>=0 && x2<width && y2<length){
                    valueMap.get(x2).set(y2, (short)(valueMap.get(x2).get(y2)+(64*incrementValue)));
                }
            }
        }
        alteredIndexes=null;
        NoiseMap.SetSeed(randNum.nextInt());
    }




    private void SmudgeDistantPerlin (double smearStrength){
        
        NoiseMap.SetSeed(randNum.nextInt());
        NoiseMap.SetFractalOctaves(1);

        ArrayList<Short> alteredIndexes = new ArrayList<Short>();

        for (short x=0; x<width;x++){
            for (short y=0; y<length; y++){
                short value = valueMap.get(x).get(y);
                byte sign = (value>=0 ? (byte)1 : (byte)-1);
                double changingValue = (short)(sign ==1 ? (((value&30720)>>11) + ((value&1984)>>6) + (value&63)) : (80-(((value&30720)>>11) + ((value&1984)>>6) + (value&63))));
                changingValue=(short)(sign*((0.0000022640842953649*(changingValue*changingValue*changingValue))-(0.00016513723545357*(changingValue*changingValue))+(0.02418958934595*(changingValue))+(0.0624999999971)));
                if (sign==1){
                    if ((changingValue+(NoiseMap.GetNoise((float)((x+5)), (float)((y+19)))))<0){
                        valueMap.get(x).set(y, (short)(value+(short)32768));
                        alteredIndexes.add(x);
                        alteredIndexes.add(y);
                    }
                }
                else{
                    if ((changingValue+(NoiseMap.GetNoise((float)((x+5)), (float)((y+19)))))>=0){
                        valueMap.get(x).set(y, (short)(value&32767));
                        alteredIndexes.add(x);
                        alteredIndexes.add(y);
                    }
                }
            }
        }

        short x;
        short x2;
        short y;
        short y2;
        byte incrementValue;

        for (int a=0;a<alteredIndexes.size();a+=2){

            x = alteredIndexes.get(a);
            y = alteredIndexes.get(a+1);
            incrementValue=(byte)(valueMap.get(x).get(y)>=0 ? 1: -1);
            for (int b=0; b<8; b++){
                x2=(short)(x+MovePatternsAdjacent[b][0]);
                y2=(short)(y+MovePatternsAdjacent[b][1]);
                if (x2>=0 && y2>=0 && x2<width && y2<length){
                    valueMap.get(x2).set(y2, (short)(valueMap.get(x2).get(y2)+(2048*incrementValue)));
                }
            }
            for (int b=0; b<16; b++){
                x2=(short)(x+MovePatternsNonAdjacent[b][0]);
                y2=(short)(y+MovePatternsNonAdjacent[b][1]);
                if (x2>=0 && y2>=0 && x2<width && y2<length){
                    valueMap.get(x2).set(y2, (short)(valueMap.get(x2).get(y2)+(64*incrementValue)));
                }
            }
            for (int b=0; b<56; b++){
                x2=(short)(x+MovePatternsDistant[b][0]);
                y2=(short)(y+MovePatternsDistant[b][1]);
                if (x2>=0 && y2>=0 && x2<width && y2<length){
                    valueMap.get(x2).set(y2, (short)(valueMap.get(x2).get(y2)+incrementValue));
                }
            }
        }
        alteredIndexes=null;
        NoiseMap.SetSeed(randNum.nextInt());
    }

     private void SmudgeDistantWhite (double smearStrength){
        
        NoiseMap.SetSeed(randNum.nextInt());
        NoiseMap.SetFractalOctaves(1);

        ArrayList<Short> alteredIndexes = new ArrayList<Short>();

        for (int x=0; x<width;x++){
            for (int y=0; y<length; y++){
                short value = valueMap.get(x).get(y);
                byte sign = (value>=0 ? (byte)1 : (byte)-1);
                value = (short)(((value&30720)>>11)+((value&1984)>>6) + (value&63));
                value=(short)(sign*((0.0000022640842953649*(value*value*value))-(0.00016513723545357*(value*value))+(0.02418958934595*(value))+(0.0624999999971)));
                if (sign==1){
                    if ((value+(randNum.nextDouble()))<0){
                        valueMap.get(x).set(y, (short)(value+(short)-32768));
                        alteredIndexes.add((short)x);
                        alteredIndexes.add((short)y);
                    }
                }
                else{
                    if ((value+(NoiseMap.GetNoise((float)((x+5)), (float)((y+19)))))>=0){
                        valueMap.get(x).set(y, (short)(value&32767));
                        alteredIndexes.add((short)x);
                        alteredIndexes.add((short)y);
                    }
                }
            }
        }

        short x;
        short x2;
        short y;
        short y2;
        byte incrementValue;

        for (int a=0;a<alteredIndexes.size();a+=2){

            x = alteredIndexes.get(a);
            y = alteredIndexes.get(a+1);
            incrementValue=(byte)(valueMap.get(x).get(y)>=0 ? 1: -1);
            for (int b=0; b<8; b++){
                x2=(short)(x+MovePatternsAdjacent[b][0]);
                y2=(short)(y+MovePatternsAdjacent[b][1]);
                if (x2>=0 && y2>=0 && x2<width && y2<length){
                    valueMap.get(x).set(y, (short)(valueMap.get(x).get(y)+(2048*incrementValue)));
                }
            }
            for (int b=0; b<16; b++){
                x2=(short)(x+MovePatternsNonAdjacent[b][0]);
                y2=(short)(y+MovePatternsNonAdjacent[b][1]);
                if (x2>=0 && y2>=0 && x2<width && y2<length){
                    valueMap.get(x).set(y, (short)(valueMap.get(x).get(y)+(64*incrementValue)));
                }
            }
            for (int b=0; b<56; b++){
                x2=(short)(x+MovePatternsDistant[b][0]);
                y2=(short)(y+MovePatternsDistant[b][1]);
                if (x2>=0 && y2>=0 && x2<width && y2<length){
                    valueMap.get(x).set(y, (short)(valueMap.get(x).get(y)+incrementValue));
                }
            }
        }
        alteredIndexes=null;
        NoiseMap.SetSeed(randNum.nextInt());
    }




    private void FastZoom(){ //doesn't update third ring 

        ArrayList<ArrayList<Short>> returnMap = new ArrayList<ArrayList<Short>>();
        int state=0;

        for (int x=0; x<width;x++){
            returnMap.add(new ArrayList<Short>());
            returnMap.add(new ArrayList<Short>());
            for (int y=0;y<length;y++){
                state=0;
                for (int i=0;i<8;i++){
                    short x2=(short)(x+MovePatternsAdjacent[i][0]);
                    short y2=(short)(y+MovePatternsAdjacent[i][1]);
                    if (x2>=0 && y2>=0 && x2<width && y2<length){
                        if (valueMap.get(x2).get(y2)>=0){state+=(1<<i);}
                    }
                }
                returnMap.get(2*x).add((short)((valueMap.get(x).get(y)>=0 ? 0 : (short)32768) + Project.asset.ZoomReferences[state][0] + (valueMap.get(x).get(y)&63) + (valueMap.get(x).get(y)>=0 ? 0 : -3*2048)));
                returnMap.get(2*x).add((short)((valueMap.get(x).get(y)>=0 ? 0 : (short)32768) + Project.asset.ZoomReferences[state][1] + (valueMap.get(x).get(y)&63) + (valueMap.get(x).get(y)>=0 ? 0 : -3*2048)));
                returnMap.get(2*x+1).add((short)((valueMap.get(x).get(y)>=0 ? 0 : (short)32768) + Project.asset.ZoomReferences[state][2] + (valueMap.get(x).get(y)&63) + (valueMap.get(x).get(y)>=0 ? 0 : -3*2048)));
                returnMap.get(2*x+1).add((short)((valueMap.get(x).get(y)>=0 ? 0 : (short)32768) + Project.asset.ZoomReferences[state][3] + (valueMap.get(x).get(y)&63) + (valueMap.get(x).get(y)>=0 ? 0 : -3*2048)));
            }
        }
        valueMap=returnMap;
        returnMap=null;
        width = (short)valueMap.size();
        length = (short)valueMap.get(0).size();
    }



    public void RefreshThirdRing(){
        for (int x=0; x<width;x++){
            for  (int y=0; y<length;y++){
                valueMap.get(x).set(y, (short)((valueMap.get(x).get(y)&(65472))));
            }
        }

        for (int x=0; x<width;x++){
            for  (int y=0; y<length;y++){
                if (valueMap.get(x).get(y)>=0){
                    for (int i=0; i<56;i++){
                        short x2=(short)(x+MovePatternsDistant[i][0]);
                        short y2=(short)(y+MovePatternsDistant[i][1]);
                        if (x2>=0 && y2>=0 && x2<width && y2<length){
                            valueMap.get(x2).set(y2, (short)(valueMap.get(x2).get(y2)+1));
                        }
                    }
                }
            }
        }
    }




    private void divideFalseDesertsDistant(double threshold){

        ArrayList<Short> AlteredIndexes= new ArrayList<Short>();

        for (short x=0; x<width; x++){
            for (short y=0; y<length; y++){
                if (valueMap.get(x).get(y)<0 && (valueMap.get(x).get(y)&63)== 0 && NoiseMap.GetNoise((float)(x),(float)(y))>threshold){
                    valueMap.get(x).set(y, (short)(valueMap.get(x).get(y)&32767));
                    AlteredIndexes.add(x);
                    AlteredIndexes.add(y);
                }
            }
        }

        for (int i=0; i<AlteredIndexes.size();i+=2){
            short x=AlteredIndexes.get(i);
            short y=AlteredIndexes.get(i+1);
            for (int a=0; a<8; a++){
                short x2=(short)(x+MovePatternsAdjacent[a][0]);
                short y2=(short)(y+MovePatternsAdjacent[a][1]);
                if (x2>=0 && y2>=0 && x2<width && y2<length){
                    valueMap.get(x2).set(y2, (short)(valueMap.get(x2).get(y2)+2048));
                }
            }
            for (int a=0; a<16; a++){
                short x2=(short)(x+MovePatternsNonAdjacent[a][0]);
                short y2=(short)(y+MovePatternsNonAdjacent[a][1]);
                if (x2>=0 && y2>=0 && x2<width && y2<length){
                    valueMap.get(x2).set(y2, (short)(valueMap.get(x2).get(y2)+64));
                }
            }
            for (int a=0; a<56; a++){
                short x2=(short)(x+MovePatternsDistant[a][0]);
                short y2=(short)(y+MovePatternsDistant[a][1]);
                if (x2>=0 && y2>=0 && x2<width && y2<length){
                    valueMap.get(x2).set(y2, (short)(valueMap.get(x2).get(y2)+1));
                }
            }
        }
        AlteredIndexes=null;
        NoiseMap.SetSeed(randNum.nextInt());
    }




    private void bridgeLand(double threshold){

        ArrayList<Short> AlteredIndexes= new ArrayList<Short>();

        for (short x=0; x<width; x++){
            for (short y=0; y<length; y++){
                if (valueMap.get(x).get(y)<0 && NoiseMap.GetNoise((float)(x),(float)(y))>threshold){
                    valueMap.get(x).set(y, (short)(valueMap.get(x).get(y)&32767));
                    AlteredIndexes.add(x);
                    AlteredIndexes.add(y);
                }
            }
        }

        for (int i=0; i<AlteredIndexes.size();i+=2){
            short x=AlteredIndexes.get(i);
            short y=AlteredIndexes.get(i+1);
            for (int a=0; a<8; a++){
                short x2=(short)(x+MovePatternsAdjacent[a][0]);
                short y2=(short)(y+MovePatternsAdjacent[a][1]);
                if (x2>=0 && y2>=0 && x2<width && y2<length){
                    valueMap.get(x2).set(y2, (short)(valueMap.get(x2).get(y2)+2048));
                }
            }
            for (int a=0; a<16; a++){
                short x2=(short)(x+MovePatternsNonAdjacent[a][0]);
                short y2=(short)(y+MovePatternsNonAdjacent[a][1]);
                if (x2>=0 && y2>=0 && x2<width && y2<length){
                    valueMap.get(x2).set(y2, (short)(valueMap.get(x2).get(y2)+64));
                }
            }
            for (int a=0; a<56; a++){
                short x2=(short)(x+MovePatternsDistant[a][0]);
                short y2=(short)(y+MovePatternsDistant[a][1]);
                if (x2>=0 && y2>=0 && x2<width && y2<length){
                    valueMap.get(x2).set(y2, (short)(valueMap.get(x2).get(y2)+1));
                }
            }
        }
        NoiseMap.SetSeed(randNum.nextInt());
        AlteredIndexes=null;
    }





    private void divideFalseDesertsLocal(int chanceToBecomeTrueDenom){

        ArrayList<Short> AlteredIndexes= new ArrayList<Short>();

        for (short x=0; x<width; x++){
            for (short y=0; y<length; y++){
                if (valueMap.get(x).get(y)<0 && (valueMap.get(x).get(y)&32704)== 0 && randNum.nextInt(chanceToBecomeTrueDenom-1)==0){
                    valueMap.get(x).set(y, (short)(valueMap.get(x).get(y)&32767));
                    AlteredIndexes.add(x);
                    AlteredIndexes.add(y);
                }
            }
        }

        for (int i=0; i<AlteredIndexes.size();i+=2){
            short x=AlteredIndexes.get(i);
            short y=AlteredIndexes.get(i+1);
            for (int a=0; a<8; a++){
                short x2=(short)(x+MovePatternsAdjacent[a][0]);
                short y2=(short)(y+MovePatternsAdjacent[a][1]);
                if (x2>=0 && y2>=0 && x2<width && y2<length){
                    valueMap.get(x2).set(y2, (short)(valueMap.get(x2).get(y2)+2048));
                }
            }
            for (int a=0; a<16; a++){
                short x2=(short)(x+MovePatternsNonAdjacent[a][0]);
                short y2=(short)(y+MovePatternsNonAdjacent[a][1]);
                if (x2>=0 && y2>=0 && x2<width && y2<length){
                    valueMap.get(x2).set(y2, (short)(valueMap.get(x2).get(y2)+64));
                }
            }
        }
        AlteredIndexes=null;
    }





    private void divideFalseDesertsAdjacent(int chanceToBecomeTrueDenom){

        ArrayList<Short> AlteredIndexes= new ArrayList<Short>();

        for (short x=0; x<width; x++){
            for (short y=0; y<length; y++){
                if (valueMap.get(x).get(y)<0 && (valueMap.get(x).get(y)&30720)== 0 && randNum.nextInt(chanceToBecomeTrueDenom-1)==0){
                    valueMap.get(x).set(y, (short)(valueMap.get(x).get(y)&32767));
                    AlteredIndexes.add(x);
                    AlteredIndexes.add(y);
                }
            }
        }

        for (int i=0; i<AlteredIndexes.size();i+=2){
            short x=AlteredIndexes.get(i);
            short y=AlteredIndexes.get(i+1);
            for (int a=0; a<8; a++){
                short x2=(short)(x+MovePatternsAdjacent[a][0]);
                short y2=(short)(y+MovePatternsAdjacent[a][1]);
                if (x2>=0 && y2>=0 && x2<width && y2<length){
                    valueMap.get(x2).set(y2, (short)(valueMap.get(x2).get(y2)+2048));
                }
            }
            for (int a=0; a<16; a++){
                short x2=(short)(x+MovePatternsNonAdjacent[a][0]);
                short y2=(short)(y+MovePatternsNonAdjacent[a][1]);
                if (x2>=0 && y2>=0 && x2<width && y2<length){
                    valueMap.get(x2).set(y2, (short)(valueMap.get(x2).get(y2)+64));
                }
            }
        }
        AlteredIndexes=null;
    }





    public void newWhiteNoiseMap(int chanceDenominatorOfTrue, short width1, short length1){

        int LandRatio=0;
        int WaterRatio=0;
        int count=0;
        width=width1;
        length=length1;

        valueMap = new ArrayList<ArrayList<Short>>();
        ArrayList<Short> AlteredIndexes = new ArrayList<Short>();

        loop : while ((WaterRatio==0 || (((double)LandRatio/WaterRatio)>0.5 || ((double)LandRatio/WaterRatio)<0.1))){
            valueMap = new ArrayList<ArrayList<Short>>();
            LandRatio=0;
            WaterRatio=0;
            AlteredIndexes = new ArrayList<Short>();
            for (short x=0; x<width; x++){
                valueMap.add(new ArrayList<Short>());
                for (short y=0; y<length; y++){
                    if (randNum.nextInt((chanceDenominatorOfTrue+1))==0){
                        valueMap.get(x).add((short)0);
                        AlteredIndexes.add(x);
                        AlteredIndexes.add(y);
                        LandRatio+=1;
                    }
                    else{
                        valueMap.get(x).add((short)32768);
                        WaterRatio+=1;
                    }
                }
            }
            if (count>100){
                System.out.println("Originally Unbalanced Continental-ness");
                break loop;
            }
            count++;
        }
        for (int a=0;a<AlteredIndexes.size();a+=2){
            nearby : for (int b=0; b<56;b++){
                byte x=(byte)(AlteredIndexes.get(a)+MovePatternsDistant[b][0]);
                byte y=(byte)(AlteredIndexes.get(a+1)+MovePatternsDistant[b][1]);
                if (x>=0 && y>=0 && x<width && y<length){
                    valueMap.get(x).set(y, (short)(valueMap.get(x).get(y)+1));
                }
                else{continue nearby;}
            }
            nearby : for (int b=0; b<16;b++){
                byte x=(byte)(AlteredIndexes.get(a)+MovePatternsNonAdjacent[b][0]);
                byte y=(byte)(AlteredIndexes.get(a+1)+MovePatternsNonAdjacent[b][1]);
                if (x>=0 && y>=0 && x<width && y<length){
                    valueMap.get(x).set(y, (short)(valueMap.get(x).get(y)+64));
                }
                else{continue nearby;}
            }
            nearby : for (int b=0; b<8;b++){
                byte x=(byte)(AlteredIndexes.get(a)+MovePatternsAdjacent[b][0]);
                byte y=(byte)(AlteredIndexes.get(a+1)+MovePatternsAdjacent[b][1]);
                if (x>=0 && y>=0 && x<width && y<length){
                    valueMap.get(x).set(y, (short)(valueMap.get(x).get(y)+2048));
                }
                else{continue nearby;}
            }
        }
    }






    public void newPerlinNoiseMap(short width1, short length1){

        int LandRatio=0;
        int WaterRatio=0;
        int count=0;
        width=width1;
        length=length1;

        valueMap = new ArrayList<ArrayList<Short>>();
        ArrayList<Short> AlteredIndexes = new ArrayList<Short>();

        loop : while ((WaterRatio==0 || (((double)LandRatio/WaterRatio)>0.9 || ((double)LandRatio/WaterRatio)<0.1))){
            valueMap = new ArrayList<ArrayList<Short>>();
            LandRatio=0;
            WaterRatio=0;
            for (int x=0; x<width; x++){
                valueMap.add(new ArrayList<Short>());
                for (int y=0; y<length; y++){
                    if (NoiseMap.GetNoise((float)((x+5)),(float)((y+19)))>=0.125){
                        valueMap.get(x).add((short)0);
                        AlteredIndexes.add((short)x);
                        AlteredIndexes.add((short)y);
                        LandRatio+=1;
                    }
                    else{
                        valueMap.get(x).add((short)-32768);
                        WaterRatio+=1;
                    }
                }
            }
            if (count>25){
                System.out.println("Originally Unbalanced Continental-ness");
                break loop;}
            count++;
            NoiseMap.SetSeed(randNum.nextInt());
        }
        for (int a=0;a<AlteredIndexes.size();a+=2){
            nearby : for (int b=0; b<56;b++){
                short x=(short)(AlteredIndexes.get(a)+MovePatternsDistant[b][0]);
                short y=(short)(AlteredIndexes.get(a+1)+MovePatternsDistant[b][1]);
                if (x>=0 && y>=0 && x<width && y<length){
                    valueMap.get(x).set(y, (short)(valueMap.get(x).get(y)+1));
                }
                else{continue nearby;}
            }
            nearby : for (int b=0; b<16;b++){
                short x=(short)(AlteredIndexes.get(a)+MovePatternsNonAdjacent[b][0]);
                short y=(short)(AlteredIndexes.get(a+1)+MovePatternsNonAdjacent[b][1]);
                if (x>=0 && y>=0 && x<width && y<length){
                    valueMap.get(x).set(y, (short)(valueMap.get(x).get(y)+64));
                }
                else{continue nearby;}
            }
            nearby : for (int b=0; b<8;b++){
                short x=(short)(AlteredIndexes.get(a)+MovePatternsAdjacent[b][0]);
                short y=(short)(AlteredIndexes.get(a+1)+MovePatternsAdjacent[b][1]);
                if (x>=0 && y>=0 && x<width && y<length){
                    valueMap.get(x).set(y, (short)(valueMap.get(x).get(y)+2048));
                }
                else{continue nearby;}
            }
        }
    }
}





