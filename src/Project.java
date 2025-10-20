import java.util.*;
import java.io.*;

enum Biomes {
            River,

            Shore, DeepOcean, ShallowOcean, LowLand, Inland,
            
            Mountains, Meadows, Hills, Plains, Desert, Swamp,
            LowLand_Plains,
            Island_hills, Island,
            
            Frozen_Ocean, Cold_Ocean, Warm_Ocean, Tropic_Ocean, Unstable_Ocean,
            Frozen_Shallows, Reef,
            Snowy_Peaks, Rocky_Peaks, Volcano,
            Snowy_Meadow, Lush_Meadow, Rocky_Meadow,
            Hilly_Tundra, Snowy_Hilled_Forest, Cold_Hilled_Forest, Redwood_Forest_Hills, Warm_Hilled_Forest, Rolling_Fields, Rocky_Desert,
            Tundra, Snowy_Forest, Cold_Forest, Redwood_Forest, Warm_Forest, Sparse_Plains, Grassland,
            Desolate_Tundra, Cold_Desert,
            
            Rocky_Hills, Snowy_Forest_Hills,
            Dry_Forest_Hills, Moderate_Forest_Hills, Lush_Forest_Hills, Jungle_Hills,
            Desolate_Forest, 
            Dry_Forest, Moderate_Forest, Lush_Forest, Jungle,
            Prairie, Rolling_Prairie,
            Rolling_Sparse_Plains, Rolling_Grassland,
            Snowy_Tundra;
        }

public class Project{
    public static int[] value= new int[256];
    public static double[] vector_table = Perlin_2D.VectorTable();

    public static Assets asset;
    public static void main(String[] args) throws IOException{
        //value = {range [0,256)}
        for (int i=0;i<256;i++){
            value[i]=i;
        }

        try{
        asset = new Assets();
        }
        catch (FileNotFoundException e){
            System.out.println("File Not Found");
            e.printStackTrace();
            System.exit(-1);
        }

        //Biome variables

        Biomes[][] BiomeMap = new Biomes[2048][2048];
        Random randNum = new Random();

        FastNoiseLite LandNoise = new FastNoiseLite();
        LandNoise.SetSeed(randNum.nextInt());
        LandNoise.SetFractalType(FastNoiseLite.FractalType.FBm);

        short[][] LandMap = FinishedStacks.newLandMap(randNum, LandNoise);

        MapMixer mixer = new MapMixer(LandMap, BiomeMap, randNum, FinishedStacks.max, FinishedStacks.min);
        BiomeMap=mixer.getBiomeMap();

        BiomeImage.GetBiomeImage(BiomeMap);

        long currentTime = System.currentTimeMillis();
        LandNoise.SetSeed(randNum.nextInt());
        LandNoise.SetFractalType(FastNoiseLite.FractalType.FBm);
        LandNoise.SetFractalGain((float)0.5);
        LandNoise.SetFractalLacunarity((float)2.3);
        LandNoise.SetFractalBounding((float)1.);
        LandNoise.SetFrequency(0.01f*1.25f);
        LandNoise.SetFractalOctaves(8);
        LandNoise.SetNoiseType(FastNoiseLite.NoiseType.OpenSimplex2S);
        

        int scale=512;
        //BufferedImage FractalTest = new BufferedImage(scale, scale, 1);

        float min=1.0f, max=-1.0f;
        float[][] Noise = new float[scale][scale];
        for (int x=0; x<scale; x++){
            for (int y=0; y<scale; y++){
                Noise[x][y]=LandNoise.GetNoise((float)x,(float)y);
                if (Noise[x][y]<min){min=Noise[x][y];}
                if (Noise[x][y]>max){max=Noise[x][y];}
            }
        }
        /*for (int x=0; x<scale; x++){
            for (int y=0; y<scale; y++){
                float value=(Noise[x][y] >= 0 ? (Noise[x][y]/max) : (Noise[x][y]-min)/(-min));
                FractalTest.setRGB(x, y, (Noise[x][y]>= 0 ? new Color(0, value, 0).getRGB() : new Color(0, 0, value).getRGB()));
            }
        }

        ImageIO.write(FractalTest, "PNG", new File("FractalTest.png"));*/

        System.err.println(System.currentTimeMillis()-currentTime + " Milliseconds, Fractal Gen Time");
        /*TODO: to do List:
         * topography map
         * perlin and fractal noise maps
         * optimizing cellular automata map{
         *      clean up
         *      optimizing spread islands and zoom blur generation functions
         *      reusing noise maps for generation
         *      using other noise types for generation
         * }
         * 
        */

        System.exit(0);
    }
}