import java.util.*;

class Vector2 {

    public float X, Y, Length;
    public double Angle;

    public Vector2(float Length1, double angle1){
        X=Length1*(float)(Math.cos(angle1));
        Y=Length1*(float)(Math.sin(angle1));
        Length=Length1;
        Angle=angle1;
    }
    public Vector2(float Origin_X, float Origin_Y, float Target_X, float Target_Y){
        X=Target_X-Origin_X;
        Y=Target_Y-Origin_Y;
        Length=(float)Math.sqrt((Math.pow((X),2))+(Math.pow((Y),2)));
        Angle=Math.asin((Y)/Length);
    }
    public Vector2(float Target_X1, float Target_Y1){
        X=Target_X1;
        Y=Target_Y1;
        Length=(float)Math.sqrt((Math.pow((X),2))+(Math.pow((Y),2)));
        Angle=Math.asin((Y)/Length);
    }

    public float getLength(){
        return Length;
    }
    public float getX(){
        return X;
    }
    public float getY(){
        return Y;
    }
    public double getAngle(){
        return Angle;
    }

    public void setLength(float Length1){
        Length=Length1;
    }
    public void setTarget_X(float Target_X1){
        X=Target_X1;
    }
    public void setTarget_Y(float Target_Y1){
        Y=Target_Y1;
    }
    public void setAngle(double Angle1){
        Angle=Angle1;
    }

    public double DotProduct(Vector2 target){
        return ((X)*(target.X))+((Y)*(target.Y));
    }

    public void Normalize(){
        Length=(float)1;
        X=(float)Math.cos(Angle);
        Y=(float)Math.sin(Angle);
    }
}

class Perlin2{
    public ArrayList<Vector2> gradientVectors = new ArrayList<Vector2>();
    public Random randNum;

    public Perlin2(Random randNum1){
        randNum=randNum1;
        for (short i=0; i<1024; i++){
            gradientVectors.add(new Vector2(1, ((i&255)/256.0)*Math.PI));
        }
        for (short i=0; i<1024; i++){
            Vector2 temp = gradientVectors.get(i);
            short index = (short)randNum.nextInt(1024);
            gradientVectors.set(i, gradientVectors.get(index));
            gradientVectors.set(index, temp);
        }
    }

    public float[][] GetMap(int scale, byte interpolationDist){
        
        float[][] Map = new float[scale][scale];

        for (float x=0; x<((double)scale)/interpolationDist;x+=(1.0/interpolationDist)){
            for (float y=0; y<((double)scale)/interpolationDist;y+=(1.0/interpolationDist)){

                int xi = (int)x;
                int yi=(int)y;

                float xf=x-xi;
                float yf=y-yi;

                Vector2 vec1 = new Vector2(xi-(1>>5), yi-(1>>5), x, y);
                Vector2 vec2 = new Vector2(xi+(1>>5)+1, yi-(1>>5), x, y);
                Vector2 vec3 = new Vector2(xi-(1>>5), yi+(1>>5)+1, x, y);
                Vector2 vec4 = new Vector2(xi+(1>>5)+1, yi+(1>>5)+1, x, y);

                xf=ComplexNoise2.SCurve(xf);
                yf=ComplexNoise2.SCurve(yf);

                /*vec1.Normalize();
                vec2.Normalize();
                vec3.Normalize();
                vec4.Normalize();/* */

                Map[(int)(x*interpolationDist)][(int)(y*interpolationDist)]=ComplexNoise2.SCurve((ComplexNoise2.Lerp(

                    ComplexNoise2.Lerp(
                        ((float)gradientVectors.get((xi)*((scale)/interpolationDist)+yi).DotProduct(vec1)), 
                        ((float)gradientVectors.get(((xi+1)*((scale)/interpolationDist)+yi)&1023).DotProduct(vec2)),
                        xf),

                    ComplexNoise2.Lerp(
                        ((float)gradientVectors.get(((xi)*((scale)/interpolationDist)+yi+1)&1023).DotProduct(vec3)), 
                        ((float)gradientVectors.get(((xi+1)*((scale)/interpolationDist)+yi+1)&1023).DotProduct(vec4)),
                        xf),

                    yf)+1)/2);
            }
        }

        return Map;
}

    //TODO: GetSinglePoint
}

class ComplexNoise2{

    public static float Lerp(float a, float b, float t){
        return a + t * (b - a);
    }

    public static float SCurve(float t){
        return t*t*(3-2*t);
    }

    public static float[][] BlendMap(float[][] OriginalMap){

        int scale = OriginalMap.length;

        float[][] returnMap = new float[scale][scale];

        int count=0;

        for (int x=0; x<scale; x++){
            for (int y=0; y<scale; y++){
                returnMap[x][y]=0;
                count=0;
                for (byte i=0; i<8; i++){
                    int x2=x+StackFunctions.MovePatternsAdjacent[i][0];
                    if (x2>=0 && x2<scale){
                        int y2=y+StackFunctions.MovePatternsAdjacent[i][1];
                        if (y2>=0 && y2<scale){
                            returnMap[x][y]+=(OriginalMap[x2][y2]);
                            count++;
                        }
                    }
                }
                returnMap[x][y]/=count;
            }
        }
        return returnMap;
    }

    public static float[][] LerpMap(float[][] OriginalMap){

        int scale = OriginalMap.length;

        scale*=4;

        float[][] returnMap = new float[scale][scale];

        for (int x=0; x<scale; x++){
            for (int y=0; y<scale; y++){
                    returnMap[x][y]=Lerp(
                        Lerp(OriginalMap[x/4][y/4], OriginalMap[(x/4+1)%(scale/4-1)][y/4], SCurve((float)((x%4)/4.0))),
                        Lerp(OriginalMap[x/4][(y/4+1)%(scale/4-1)], OriginalMap[(x/4+1)%(scale/4-1)][(y/4+1)%(scale/4-1)], SCurve((float)((x%4)/4.0))),
                        SCurve((float)((y%4)/4.0)));
                
            }
        }

        return returnMap;
    }

    public static float[][] FastPerlin(Random randNum, int scale, byte interpolationDist){
        //return LerpMap( new Perlin2(randNum).GetMap(scale/4));
        return BlendMap(BlendMap(LerpMap(new Perlin2(randNum).GetMap(scale/4, (interpolationDist)))));
    }
}
        /*biomes:
         * continental-ness:
         *      null: deep ocean
         *      null: shallow ocean
         *      null: shore
         *      null: low land
         *      null: inland
         * 
         * erosion: saved for river mixer
         *      inland: mountains
         *      inland: meadows
         *      inland: hills
         *      inland: plains
         *      inland: deserts
         * 
         *      lowland: hills
         *      lowland: Lowland plains
         *      lowland: swamps
         * 
         *      deep ocean: archipelago hills
         *      deep ocean: archipelago
         *      deep ocean: shallow ocean
         * 
         * temperature:
         *      deep ocean: frozen ocean
         *      deep ocean: cold ocean
         *      deep ocean: warm ocean
         *      deep ocean: tropic ocean
         *      deep ocean: unstable ocean
         * 
         *      shallow ocean: frozen shallows
         *      (shallow ocean: shallow ocean)
         *      shallow ocean: reef
         * 
         *      mountains: snowy peaks
         *      mountains: rocky peaks
         *      mountains: volcano
         * 
         *      meadows: snowy meadow
         *      meadows: lush meadow
         *      meadows: rocky meadow
         * 
         *      hills: hilly tundra
         *      hills: snowy hilled forest
         *      hills: cold hilly forest
         *      hills: hilly redwood forest
         *      hills: warm hilly forest
         *      hills: rolling sparse plains
         *      hills: rocky desert
         * 
         *      plains: tundra
         *      plains: snowy forest
         *      plains: cold forest
         *      plains: redwood forest
         *      plains: warm forest
         *      plains: sparse plains
         *      plains: grassland
         * 
         *      Lowland plains: tundra
         *      Lowland plains: snowy forest
         *      Lowland plains: cold forest
         *      Lowland plains: warm forest
         *      Lowland plains: sparse plains
         * 
         *      swamp: snowy forest
         *      swamp: cold forest
         *      swamp: warm forest
         *      (swamp: swamp)
         * 
         *      desert: desolate tundra
         *      desert: cold desert
         *      (desert: desert)
         * 
         *      archipelago hills: snowy hilled forest
         *      archipelago hills: cold hilled forest
         *      archipelago hills: warm hilly forest
         * 
         *      archipelago: snowy forest
         *      archipelago: cold forest
         *      archipelago: warm forest
         *      archipelago: sparse plains
         * 
         * humidity: //
         * 
         *      1/4 dry
         *      5/12 medium
         *      1/3 snowy/wet
         * 
         *      snowy hilled forest: cold forest
         *      (snowy hilled forest: snowy hilled forest)
         * 
         *      cold hilly forest: rocky hills
         *      cold hilly forest: cold forest hills
         * 
         *      warm hilly forest: dry forest hills
         *      warm hilly forest: moderate forest hills
         *      warm hilly forest: lush forest hills
         *      warm hilly forest: jungle hills
         * 
         *      cold forest: desolate forest
         *      cold forest: cold forest
         * 
         *      hilly redwood forest: rocky forest hills
         *      hilly redwood forest: cold forest hills
         *      (hilly redwood forest: hilly redwood forest)
         * 
         *      redwood forest: desolate forest
         *      redwood forest: cold forest
         *      (redwood forest: redwood forest)
         * 
         *      warm forest: dry forest
         *      warm forest: moderate forest
         *      warm forest: lush forest
         *      warm forest: jungle
         * 
         *      snowy forest: cold forest
         *      (snowy forest: snowy forest)
         * 
         *      grassland: desert
         *      grassland: prairie
         *      grassland: sparse plains
         *      (grassland: grassland)
         * 
         *      sparse plains: Prairie
         *      (sparse plains: sparse plains)
         *      sparse plains: grassland
         *      
         *      rolling sparse plains: rolling Prairie
         *      (rolling sparse plains: rolling sparse plains)
         *      rolling sparse plains: rolling grassland
         * 
         *      (rocky desert: rocky desert)
         *      rocky desert: rolling Prairie
         *      rocky desert: rolling sparse plains
         * 
         *      desolate tundra: desolate forest
         *      desolate tundra: snowy tundra
         * 
         *      swamp: sparse plains
         *      swamp: warm forest
         *      (swamp: swamp)
         * 
         *      desert: desert
         *      desert: Prairie
         *      desert: sparse plains
         *      desert: grassland
         *      desert: swamp
         */


         //Map/Final biomes:
         /*shallow ocean, shore, 
         deserts(0), swamps(0), 

         frozen ocean (-2), cold ocean (-2), 
         warm ocean (-2), tropic ocean (-2), unstable ocean (-2), 

         frozen shallows (-1), reef (-1), 
         snowy peaks (5), rocky peaks (5), volcano (5), 
         hilly tundra (3), hilly snowy forest (3), hilly cold forest (3), 
         hilly redwood forest(3), rolling sparse plains (2.5), 

         snowy meadow(4), lush meadow(4), rocky meadow(4),
         
         tundra(2), snowy forest(2), cold forest (2), 
         redwood forest (2), sparse plains (1), 

         grassland (1), 
         cold desert (0), rocky hills (3), 
         dry forest hills (3), moderate forest hills (3), lush forest hills(3), jungle hills(3),

         desolate forest(0),
         dry forest(2), moderate forest(2), lush forest(2), jungle(2), 
         Prairie(1), rolling Prairie(2.5), rolling grassland(2.5), 
         snowy tundra(2)*/

         /*shallow ocean, shore, 
         deserts(yellow), swamps(brown), 

         frozen ocean (light blue to white), cold ocean (sky blue), 
         warm ocean (blue), tropic ocean (saturated blue to purplish blue), unstable ocean (purple), 

         frozen shallows (cream muddy white), reef (coral orange), 
         snowy peaks (white), rocky peaks (grey), volcano (red), 
         hilly tundra (very light green), hilly snowy forest (light green), hilly cold forest (brown-green), 
         hilly redwood forest(reddish brown), rolling sparse plains (green), 

         snowy meadow(red), lush meadow(blue), rocky meadow(coral orange),
         
         tundra(very light green), snowy forest(light green), cold forest (brown-green), 
         redwood forest (reddish brown), sparse plains (green), 

         grassland (blueish green), 
         cold desert (light yellow), rocky hills (light brown), 
         dry forest hills (greyish brown), moderate forest hills (purple), lush forest hills(pink), jungle hills(orange),

         desolate forest(black),
         dry forest(greyish brown), moderate forest(purple), lush forest(pink), jungle(orange), 
         Prairie(tan), rolling Prairie(tan), rolling grassland(blueish green),
         snowy tundra(coral orange)*/

class MapMixer{

    //float[][] ErosionMap, Continental-nessMap, TempMap, HumidityMap, SlopeMap;
    float[][] SurfaceDepthMap, TopographyMap, GroundFertilityMap;
    Biomes[][] BiomeMap;

    public MapMixer(short[][] LandMap, Biomes[][] BiomeMap1, Random randNum, int LandMapMax, int LandMapMin){

        BiomeMap=BiomeMap1;

        FastNoiseLite NoiseMap= new FastNoiseLite();
        NoiseMap.SetNoiseType(FastNoiseLite.NoiseType.Perlin);
        NoiseMap.SetFractalType(FastNoiseLite.FractalType.FBm);
        NoiseMap.SetFractalOctaves(5);
        NoiseMap.SetFractalGain((float)0.4);
        NoiseMap.SetSeed(randNum.nextInt());

        float[][] ErosionMap = new float[2048][2048];
        for (short x=0; x<2048;x++){
           for (short y=0; y<2048;y++){
                ErosionMap[x][y]=NoiseMap.GetNoise((float)(x/6.0),(float)(y/6.0));
           }
        }

        NoiseMap.SetSeed(randNum.nextInt());
        float value;
        //BufferedImage BiomeMapVariablesImage = new BufferedImage(4096, 4096, 1);

        for (short x=0; x<2048;x++){
           for (short y=0; y<2048;y++){
                value= LandMap[x][y];
                if(value>0){
                    if(value>6){
                        value-=(ErosionMap[x][y]*4);
                        value+=((NoiseMap.GetNoise((float)(x/2.0),(float)(y/2.0))+1)*12);
                        if(value>LandMapMax){value=LandMapMax;}
                        if(value>32){
                            BiomeMap[x][y]=Biomes.Inland;
                        }
                        else{
                            BiomeMap[x][y]=Biomes.LowLand;
                        }
                    }
                    else{
                        BiomeMap[x][y]=Biomes.Shore;
                    }
                }
                else{
                    value-=(ErosionMap[x][y]*10);
                    if(value<LandMapMin){value=LandMapMin;}
                    if(value<-64){
                        BiomeMap[x][y]=Biomes.DeepOcean;
                    }
                    else{
                        BiomeMap[x][y]=Biomes.ShallowOcean;
                    }
                }
            }
        }
        LandMap=null;

            for (short x=0; x<2048; x++){
                iteration : for (short y=0; y<2048; y++){
                        value= ErosionMap[x][y];
                        //BiomeMapVariablesImage.setRGB(x,y, new Color(0, (int)((value+1)*127.5), 0).getRGB());

                        switch (BiomeMap[x][y]){
                            case DeepOcean:
                                /*if(value<=-0.24309){
                                    if(value<=-0.32366455856){
                                        if (value<=-0.4014){
                                            if(value<=-0.4862){
                                                BiomeMap[x][y]=Biomes.Island_hills;
                                            }
                                            else{
                                                BiomeMap[x][y]=Biomes.Island;
                                            }
                                        }
                                        else{
                                            BiomeMap[x][y]=Biomes.Shore;
                                        }
                                    }
                                    else{
                                        BiomeMap[x][y]=Biomes.ShallowOcean;
                                    }
                                }*/
                                break;
                            
                            case LowLand:
                                if(value<0.2175){
                                    if(value<-0.0722){
                                        BiomeMap[x][y]=Biomes.LowLand_Plains;
                                    }
                                    else{
                                        BiomeMap[x][y]=Biomes.Hills;
                                    }
                                }
                                else{
                                    BiomeMap[x][y]=Biomes.Swamp;
                                }
                                break;

                            case Inland:
                                if(value<0.1236){
                                    if(value<-0.006){
                                        if(value<-0.1425){
                                            if(value<-0.2087){
                                                BiomeMap[x][y]=Biomes.Mountains;
                                            }
                                            else{
                                                BiomeMap[x][y]=Biomes.Meadows;
                                            }
                                        }
                                        else{
                                            BiomeMap[x][y]=Biomes.Hills;
                                        }
                                    }
                                    else{
                                        BiomeMap[x][y]=Biomes.Plains;
                                    }
                                }
                                else{
                                    BiomeMap[x][y]=Biomes.Desert;
                                }
                                break;

                            default:
                                continue iteration;
                        }
                }
            }
            NoiseMap.SetSeed(randNum.nextInt());
            for (short x=0; x<2048; x++){
                iteration : for (short y=0; y<2048; y++){
                        value=NoiseMap.GetNoise((float)(x/6.0),(float)(y/6.0));

                        switch (BiomeMap[x][y]){
                            case DeepOcean:
                                if(value>-0.23628){
                                    if(value>-0.14243){
                                        if(value>0.04939){
                                            if(value>0.4674){
                                                BiomeMap[x][y]=Biomes.Unstable_Ocean;
                                            }
                                            else{
                                                BiomeMap[x][y]=Biomes.Tropic_Ocean;
                                            }
                                        }
                                        else{
                                            BiomeMap[x][y]=Biomes.Warm_Ocean;
                                        }
                                    }
                                    else{
                                        BiomeMap[x][y]=Biomes.Cold_Ocean;
                                    }
                                }
                                else{
                                    BiomeMap[x][y]=Biomes.Frozen_Ocean;
                                }
                                break;

                            case ShallowOcean:
                                if(value>-0.23628){
                                    if(value>0.29316){
                                        BiomeMap[x][y]=Biomes.Reef;
                                    }
                                }
                                else{
                                    BiomeMap[x][y]=Biomes.Frozen_Shallows;
                                }
                                break;
                            case Mountains:
                                if(value>0.11881){
                                    if(value>0.4674){
                                        BiomeMap[x][y]=Biomes.Volcano;
                                    }
                                    else{
                                        BiomeMap[x][y]=Biomes.Rocky_Peaks;
                                    }
                                }
                                else{
                                    BiomeMap[x][y]=Biomes.Snowy_Peaks;
                                }
                                break;
                            case Meadows:
                                if(value>-0.07229){
                                    if(value>0.2174){
                                        BiomeMap[x][y]=Biomes.Rocky_Meadow;
                                    }
                                    else{
                                        BiomeMap[x][y]=Biomes.Lush_Meadow;
                                    }
                                }
                                else{
                                    BiomeMap[x][y]=Biomes.Snowy_Meadow;
                                }
                                break;
                            case Hills:
                                if(value>-0.31197){
                                    if(value>-0.14243){
                                        if(value>-0.009405){
                                            if(value>-0.005543){
                                                if(value>0.12851){
                                                    if(value>0.22512){
                                                        BiomeMap[x][y]=Biomes.Rocky_Desert;
                                                    }
                                                    else{
                                                        BiomeMap[x][y]=Biomes.Rolling_Sparse_Plains;
                                                    }
                                                }
                                                else{
                                                    BiomeMap[x][y]=Biomes.Warm_Hilled_Forest;
                                                }
                                            }
                                            else{
                                                BiomeMap[x][y]=Biomes.Redwood_Forest_Hills;
                                            }
                                        }
                                        else{
                                            BiomeMap[x][y]=Biomes.Cold_Hilled_Forest;
                                        }
                                    }
                                    else{
                                        BiomeMap[x][y]=Biomes.Snowy_Forest_Hills;
                                    }
                                }
                                else{
                                    BiomeMap[x][y]=Biomes.Hilly_Tundra;
                                }
                                break;
                            case Plains:
                                if(value>-0.31197){
                                    if(value>-0.14243){
                                        if(value>-0.009405){
                                            if(value>-0.005543){
                                                if(value>0.12851){
                                                    if(value>0.21747){
                                                        BiomeMap[x][y]=Biomes.Grassland;
                                                    }
                                                    else{
                                                        BiomeMap[x][y]=Biomes.Sparse_Plains;
                                                    }
                                                }
                                                else{
                                                    BiomeMap[x][y]=Biomes.Warm_Forest;
                                                }
                                            }
                                            else{
                                                BiomeMap[x][y]=Biomes.Redwood_Forest;
                                            }
                                        }
                                        else{
                                            BiomeMap[x][y]=Biomes.Cold_Forest;
                                        }
                                    }
                                    else{
                                        BiomeMap[x][y]=Biomes.Snowy_Forest;
                                    }
                                }
                                else{
                                    BiomeMap[x][y]=Biomes.Tundra;
                                }
                                break;
                            case LowLand_Plains:
                                if(value>-0.31197){
                                    if(value>-0.14243){
                                        if(value>-0.009405){
                                            if(value>0.12851){
                                                    BiomeMap[x][y]=Biomes.Sparse_Plains;
                                            }
                                            else{
                                                BiomeMap[x][y]=Biomes.Warm_Forest;
                                            }
                                        }
                                        else{
                                            BiomeMap[x][y]=Biomes.Cold_Forest;
                                        }
                                    }
                                    else{
                                        BiomeMap[x][y]=Biomes.Snowy_Forest;
                                    }
                                }
                                else{
                                    BiomeMap[x][y]=Biomes.Tundra;
                                }
                                break;
                            case Swamp:
                                if(value>-0.14243){
                                    if(value>-0.009405){
                                        if(value<=0.05344){
                                           BiomeMap[x][y]=Biomes.Warm_Forest;
                                        }
                                    }
                                    else{
                                        BiomeMap[x][y]=Biomes.Cold_Forest;
                                    }
                                }
                                else{
                                    BiomeMap[x][y]=Biomes.Snowy_Forest;
                                }
                                break;
                            case Desert:
                                if(value>-0.14243){
                                    if(value<=-0.009405){
                                        BiomeMap[x][y]=Biomes.Cold_Desert;
                                    }
                                }
                                else{
                                    BiomeMap[x][y]=Biomes.Desolate_Tundra;
                                }
                                break;
                            case Island_hills:
                                if(value>-0.14243){
                                    if(value>-0.009405){
                                        BiomeMap[x][y]=Biomes.Warm_Hilled_Forest;
                                    }
                                    else{
                                        BiomeMap[x][y]=Biomes.Cold_Hilled_Forest;
                                    }
                                }
                                else{
                                    BiomeMap[x][y]=Biomes.Snowy_Forest_Hills;
                                }
                                break;
                            case Island:
                                if(value>-0.14243){
                                    if(value>-0.009405){
                                        if(value>0.12851){
                                            BiomeMap[x][y]=Biomes.Sparse_Plains;
                                        }
                                        else{
                                            BiomeMap[x][y]=Biomes.Warm_Forest;
                                        }
                                    }
                                    else{
                                        BiomeMap[x][y]=Biomes.Cold_Forest;
                                    }
                                }
                                else{
                                    BiomeMap[x][y]=Biomes.Snowy_Forest;
                                }
                                break;
                            
                            default:
                                continue iteration;
                        }
                }
            }
            NoiseMap.SetSeed(randNum.nextInt());
            for (short x=0; x<2048; x++){
                iteration : for (short y=0; y<2048; y++){
                        value=NoiseMap.GetNoise((float)(x/12.0),(float)(y/12.0));

                        switch (BiomeMap[x][y]){

                            case Snowy_Forest_Hills:
                                if(value<=0.07554){
                                    BiomeMap[x][y]=Biomes.Cold_Hilled_Forest;
                                }
                                break;

                            case Cold_Hilled_Forest:
                                if(value<=-0.14243){
                                    BiomeMap[x][y]=Biomes.Rocky_Hills;
                                }
                                break;
                            
                            case Warm_Hilled_Forest:
                                if(value>-0.14243){
                                    if(value>-0.05091){
                                        if(value>0.07554){
                                            BiomeMap[x][y]=Biomes.Jungle_Hills;
                                        }
                                        else{
                                            BiomeMap[x][y]=Biomes.Lush_Forest_Hills;
                                        }
                                    }
                                    else{
                                        BiomeMap[x][y]=Biomes.Moderate_Forest_Hills;
                                    }
                                }
                                else{
                                    BiomeMap[x][y]=Biomes.Dry_Forest_Hills;
                                }
                                break;

                            case Cold_Forest:
                                if(value<=-0.14243){
                                    BiomeMap[x][y]=Biomes.Tundra;
                                }

                            case Redwood_Forest_Hills:
                                if(value>-0.14243){
                                    if(value<=0.07554){
                                        BiomeMap[x][y]=Biomes.Cold_Hilled_Forest;
                                    }
                                }
                                else{
                                    BiomeMap[x][y]=Biomes.Rocky_Hills;
                                }
                                break;

                            case Redwood_Forest:
                                if(value>-0.14243){
                                    if(value<=0.07554){
                                        BiomeMap[x][y]=Biomes.Cold_Forest;
                                    }
                                }
                                else{
                                    BiomeMap[x][y]=Biomes.Tundra;
                                }
                                break;

                            case Warm_Forest:
                                if(value>-0.14243){
                                    if(value>-0.05091){
                                        if(value>0.07554){
                                            BiomeMap[x][y]=Biomes.Jungle;
                                        }
                                        else{
                                            BiomeMap[x][y]=Biomes.Lush_Forest;
                                        }
                                    }
                                    else{
                                        BiomeMap[x][y]=Biomes.Moderate_Forest;
                                    }
                                }
                                else{
                                    BiomeMap[x][y]=Biomes.Dry_Forest;
                                }
                                break;
                            
                            case Snowy_Forest:
                                if(value<=0.07554){
                                    BiomeMap[x][y]=Biomes.Cold_Forest;
                                }
                                break;

                            case Grassland:
                                if(value>-0.23628){
                                    if(value>-0.14243){
                                        if(value<=0.07554){
                                            BiomeMap[x][y]=Biomes.Sparse_Plains;
                                        }
                                    }
                                    else{
                                        BiomeMap[x][y]=Biomes.Prairie;
                                    }
                                }
                                else{
                                    BiomeMap[x][y]=Biomes.Desert;
                                }
                                break;

                            case Sparse_Plains:
                                if(value>-0.14243){
                                    if(value>0.07554){
                                        BiomeMap[x][y]=Biomes.Grassland;
                                    }
                                }
                                else{
                                    BiomeMap[x][y]=Biomes.Prairie;
                                }
                                break;

                            case Rolling_Sparse_Plains:
                                if(value>-0.2086){
                                    if (value>0.10481){
                                        BiomeMap[x][y]=Biomes.Rolling_Grassland;
                                    }
                                }
                                else{
                                    BiomeMap[x][y]=Biomes.Rolling_Prairie;
                                }
                                break;

                            case Rocky_Desert:
                                if(value>-0.2086){
                                    if(value>-0.14243){
                                        BiomeMap[x][y]=Biomes.Rolling_Sparse_Plains;
                                    }
                                    else{
                                        BiomeMap[x][y]=Biomes.Rolling_Prairie;
                                    }
                                }
                                break;

                            case Desolate_Tundra:
                                if(value>0.10481){
                                    BiomeMap[x][y]=Biomes.Snowy_Tundra;
                                }
                                else{
                                    BiomeMap[x][y]=Biomes.Cold_Desert;
                                }
                                break;

                            case Swamp:
                                if(value>-0.14243){
                                    if(value<=0.07554){
                                        if(value>-0.05091){
                                            BiomeMap[x][y]=Biomes.Lush_Forest;
                                        }
                                        else{
                                            BiomeMap[x][y]=Biomes.Moderate_Forest;
                                        }
                                    }
                                }
                                else{
                                    BiomeMap[x][y]=Biomes.Sparse_Plains;
                                }
                                break;

                            case Desert:
                                if(value>-0.14897){
                                    if(value>-0.09436){
                                        if(value>0.07554){
                                            if(value>0.20464){
                                                BiomeMap[x][y]=Biomes.Swamp;
                                            }
                                            else{
                                                BiomeMap[x][y]=Biomes.Grassland;
                                            }
                                        }
                                        else{
                                            BiomeMap[x][y]=Biomes.Sparse_Plains;
                                        }
                                    }
                                    else{
                                        BiomeMap[x][y]=Biomes.Prairie;
                                    }
                                }
                                break;
                            default:
                                continue iteration;
                        }
                }
            }

        /*TODO: Biome Look Up Tables
        * Erosion, Temp, Humidity Noise Maps Configuration
        * River map, Surface Depth map, Slope map, continental-ness map Constructors
        * Ground Fertility, Topography, Biome, Slope map Constructors/Mixers
        * Topography network (scaling weight, activation functions and splines)
        * Visualizations
        */
        
        /*Rivers:
         * Fractal ridging erosion map to hopefully draw rivers at the edge of mountains and high ground as run off
         * group rivers with flood fill or other algorithm
         * use A* algorithm with topography map ot slope to connect rivers and paint lakes in depressions as a by product
         * possibly paint lakes using height map as a simple y floor
         */

         /*slope
          * angle and scale factor
          * change in height between blocks at x±1 / 2.0 
          repeat for y axis
          take atan or tan^-1
          divide delta x by cos theta for scale factor
          * merges x and y 1d slope maps
          * maybe generate before terrain to dictate terrain and make more continuous
          */
    }
    public Biomes[][] getBiomeMap(){
        return BiomeMap;
    }
    public float[][] getTopographyMap(){
        return TopographyMap;
    }
    public float[][] getSurfaceDepthMap(){
        return SurfaceDepthMap;
    }
    public float[][] getFertilityMap(){
        return GroundFertilityMap;
    }
}