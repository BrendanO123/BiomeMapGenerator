import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
public class Assets {
    
    public short[][] ZoomReferences = new short[256][4];

    public Assets() throws FileNotFoundException{
        File listText = new File("Dependencies/OutputList.txt");
        Scanner fileScanner = new Scanner(listText);
        fileScanner.useDelimiter(" ");
        for (int a=0;a<256;a++){
            for (int b=0; b<4; b++){
                ZoomReferences[a][b]=(short)(fileScanner.nextInt());
            }
        }
        fileScanner.close();
    }
    
    public void RefreshZoomReferences(){
        int state=0;
        short[][] startingMap = new short[3][3];
        short[][] scaledMap = new short[6][6];
        short valueShort;
        byte x2;
        byte y2;
        File listText = new File("Dependencies/OutputList.txt");
        try {
            FileWriter writer = new FileWriter(listText);
            for (int i=0;i<256;i++){
                startingMap = new short[3][3];
                scaledMap = new short[6][6];
                state=(i);
                startingMap[1][1]=(short)0;
                for (int a=0;a<8;a++){
                    if (Math.abs(state&(1<<a))>0){startingMap[StackFunctions.MovePatternsAdjacent[a][0]+1][StackFunctions.MovePatternsAdjacent[a][1]+1]=0;}
                    else {startingMap[StackFunctions.MovePatternsAdjacent[a][0]+1][StackFunctions.MovePatternsAdjacent[a][1]+1]=(short)32768;}
                }
                for (int x=0;x<3;x++){
                    for (int y=0; y<3; y++){
                        valueShort=startingMap[x][y];
                        scaledMap[2*x][2*y]=valueShort;
                        scaledMap[2*x+1][2*y]=valueShort;
                        scaledMap[2*x][2*y+1]=valueShort;
                        scaledMap[2*x+1][2*y+1]=valueShort;
                    }
                }
                for (int x=0; x<6;x++){
                    for (int y=0;y<6; y++){
                        if (scaledMap[x][y]>=0){
                            for (int a=0; a<8;a++){
                                x2=(byte)(x+StackFunctions.MovePatternsAdjacent[a][0]);
                                y2=(byte)(y+StackFunctions.MovePatternsAdjacent[a][1]);
                                if (x2>=2 && y2>=2 && x2<4 && y2<4){
                                    scaledMap[x2][y2]+=2048;
                                }
                            }
                            for (int a=0; a<16;a++){
                                x2=(byte)(x+StackFunctions.MovePatternsNonAdjacent[a][0]);
                                y2=(byte)(y+StackFunctions.MovePatternsNonAdjacent[a][1]);
                                if (x2>=2 && y2>=2 && x2<4 && y2<4){
                                    scaledMap[x2][y2]+=64;
                                }
                            }
                        }
                    }
                }
                writer.write((scaledMap[2][2] + " " + scaledMap[2][3] + " " + scaledMap[3][2] + " " + scaledMap[3][3] +  (i!=255 ? " " : "")));
            }
            writer.close();
            System.out.println("\nZoom References Refresh Successful\n");
        } 
        catch (IOException e) {
            System.out.println("An Error Occurred");
            e.printStackTrace();
        }
    }
    final byte[][] MovePatternsAdjacentUpdate = {
        {-1,-1}, {0,-1}, {1,-1},
        {-1,0},           {1,0},
        {-1,1},  {0,1},  {1,1}
    };
    public void RefreshZoomReferencesChar(){
        int state=0;
        short[][] startingMap = new short[3][3];
        short[][] scaledMap = new short[6][6];
        short valueShort;
        byte x2;
        byte y2;
        File listText = new File("Dependencies/OutputListChar.txt");
        try {
            FileWriter writer = new FileWriter(listText);
            for (int i=0;i<256;i++){
                startingMap = new short[3][3];
                scaledMap = new short[6][6];
                state=(i);
                startingMap[1][1]=(short)0;
                for (int a=0;a<8;a++){
                    if (Math.abs(state&(1<<a))>0){startingMap[MovePatternsAdjacentUpdate[a][0]+1][MovePatternsAdjacentUpdate[a][1]+1]=0;}
                    else {startingMap[MovePatternsAdjacentUpdate[a][0]+1][MovePatternsAdjacentUpdate[a][1]+1]=(short)32768;}
                }
                for (int x=0;x<3;x++){
                    for (int y=0; y<3; y++){
                        valueShort=startingMap[x][y];
                        scaledMap[2*x][2*y]=valueShort;
                        scaledMap[2*x+1][2*y]=valueShort;
                        scaledMap[2*x][2*y+1]=valueShort;
                        scaledMap[2*x+1][2*y+1]=valueShort;
                    }
                }
                for (int x=0; x<6;x++){
                    for (int y=0;y<6; y++){
                        if (scaledMap[x][y]>=0){
                            for (int a=0; a<8;a++){
                                x2=(byte)(x+StackFunctions.MovePatternsAdjacent[a][0]);
                                y2=(byte)(y+StackFunctions.MovePatternsAdjacent[a][1]);
                                if ((x2>=2) && (y2>=2) && (x2<4) && (y2<4)){
                                    scaledMap[x2][y2]+=2048;
                                }
                            }
                            for (int a=0; a<16;a++){
                                x2=(byte)(x+StackFunctions.MovePatternsNonAdjacent[a][0]);
                                y2=(byte)(y+StackFunctions.MovePatternsNonAdjacent[a][1]);
                                if (x2>=2 && y2>=2 && x2<4 && y2<4){
                                    scaledMap[x2][y2]+=64;
                                }
                            }
                        }
                    }
                }
                writer.write((char)(scaledMap[2][2]));
                writer.write((char)(scaledMap[2][3]));
                writer.write((char)(scaledMap[3][2]));
                writer.write((char)(scaledMap[3][3]));
            }
            writer.close();
            System.out.println("\nZoom References Refresh Successful\n");
        } 
        catch (IOException e) {
            System.out.println("An Error Occurred");
            e.printStackTrace();
        }
    }
}
