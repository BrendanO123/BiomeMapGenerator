import java.text.ParseException;
import java.util.*;
import java.io.*;

public class FinishedStacks{

    public static int max, min;

    public static short[][] newLandMap(Random randNum, FastNoiseLite LandNoise) throws IOException{
        LandNoise.SetSeed(randNum.nextInt());
        LandNoise.SetFractalType(FastNoiseLite.FractalType.FBm);

        StackFunctions LandMap = new StackFunctions(randNum, LandNoise);

        LandMap.newWhiteNoiseMap(10, (short)16,(short)16);

        StackFunctions.Scale_Type Local = StackFunctions.Scale_Type.Local;
        StackFunctions.Scale_Type Adjacent = StackFunctions.Scale_Type.Adjacent;
        StackFunctions.Scale_Type Distant = StackFunctions.Scale_Type.Distant;

        StackFunctions.Noise_Type Perlin = StackFunctions.Noise_Type.Perlin;

        long previousTime =System.currentTimeMillis();
        final long initialTime=System.currentTimeMillis();

        ArrayList<Integer> Problems = new ArrayList<Integer>();

        short[][] DistMap = new short[2048][2048];

        try {
            LandMap.DivideFalseDeserts(Local, 2);
            LandMap.DivideFalseDeserts(Local, 2);

            LandMap.ConnectIslands(Adjacent, Integer.MAX_VALUE,Integer.MAX_VALUE,10,10,10);
            LandMap.ConnectIslands(Adjacent, Integer.MAX_VALUE,Integer.MAX_VALUE,10,15,15);
            
            LandMap.ZoomBlurred(Local, Perlin, 0.5);

            LandMap.ConnectIslands(Local, Integer.MAX_VALUE,Integer.MAX_VALUE,10,15,15);
            
            LandMap.ZoomBlurred(Local, Perlin, 0.3);

            LandMap.DivideFalseDeserts(Local, 20);

            LandMap.ConnectIslands(Local, Integer.MAX_VALUE,Integer.MAX_VALUE,8,15,10);
            LandMap.ConnectIslands(Local, Integer.MAX_VALUE,Integer.MAX_VALUE,8,25,30);
            
            LandMap.ZoomBlurred(Local, Perlin, 0.3);
            LandMap.Smudge(Local, Perlin, 0.4);
            LandMap.ConnectIslands(Local, Integer.MAX_VALUE,Integer.MAX_VALUE,8,10,10);
            LandMap.ConnectIslands(Local, Integer.MAX_VALUE,Integer.MAX_VALUE,5,15,15);
            
            LandMap.ZoomBlurred(Local, Perlin, 0.3);

            LandMap.ConnectIslands(Local, Integer.MAX_VALUE,Integer.MAX_VALUE,8,10,10);
            LandMap.ConnectIslands(Local, Integer.MAX_VALUE,Integer.MAX_VALUE,8,10,10);

            LandMap.ZoomBlurred(Local, Perlin, 0.4);

            LandMap.ConnectIslands(Local, Integer.MAX_VALUE,Integer.MAX_VALUE,8,10,10);
            LandMap.ConnectIslands(Local, Integer.MAX_VALUE,Integer.MAX_VALUE,8,10,10);
            

            LandMap.ZoomBlurred(Local, Perlin, 0.3);
            LandMap.ConnectIslands(Local, 2,2,5,Integer.MAX_VALUE,Integer.MAX_VALUE);
            LandMap.ConnectIslands(Local, 2,3,5,Integer.MAX_VALUE,Integer.MAX_VALUE);
            LandMap.ConnectIslands(Local, 2,3,5,Integer.MAX_VALUE,Integer.MAX_VALUE);
            LandMap.RefreshThirdRing();
            LandMap.ZoomBlurred(Distant, Perlin, 0.4);
            
            LandMap.RemoveOutliers(Local);
            LandMap.RefreshThirdRing();
            LandMap.RemoveOutliers(Distant);

            System.out.println("\n" + (System.currentTimeMillis()-previousTime) + " Generation Time (Milliseconds)\n");
            previousTime=System.currentTimeMillis();
            DistMap=LandMap.CleanUpUpdated((short)150);
            System.out.println((System.currentTimeMillis()-previousTime) + " Clean Up Time (Milliseconds)");
        }
        catch (ParseException e){
            e.printStackTrace();
            System.exit(-1);
        }

        max =1;
        min=-1;
        for (short x=0; x<DistMap.length; x++){
            for (short y=0; y<DistMap[0].length; y++){
                if (DistMap[x][y]>max){max=(int)DistMap[x][y];}
                if (DistMap[x][y]<min){min=(int)DistMap[x][y];}
            }
        }

        Problems.addAll(LandMap.refreshInternalDebug());
        System.out.println("\n" + (Problems.size()/2) + " Internal Refresh Errors\n");
        LandMap.RefreshThirdRing();

        System.out.println((System.currentTimeMillis()-initialTime) + " Final (Milliseconds)\n");

        return DistMap;
    }
}