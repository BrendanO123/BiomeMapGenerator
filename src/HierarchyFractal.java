public class HierarchyFractal {
    public static float[][] GetFractalNoise(FastNoiseLite originalMap, int[] Starting_scale, int[]Scale){

        int starting_height=Starting_scale[0];
        int starting_width=Starting_scale[1];
        int CurrentScale =(starting_height>=starting_width ? starting_height : starting_width);

        Float CurrentAmp =1.0f;

        int height=Scale[0];
        int width=Scale[1];
        int FinalScale = (height>=width ? height : width);

        int Octaves=originalMap.GetOctaves();
        originalMap.SetFractalOctaves(1);

        float FractalGain = originalMap.GetFractalGain();

        double scaleFactor = (((double)CurrentScale)/FinalScale);
        int IterativeFactor = (int)Math.ceil(scaleFactor/Octaves);

        float[][][] Map = new float[Octaves][FinalScale][FinalScale];

        boolean last = false;

        for (int i=0; i<Octaves; i++){


            if (i==Octaves) {CurrentScale=FinalScale; last = true;}
            int spreadingFactor = (int) Math.ceil(((double)FinalScale)/CurrentScale);

            for (int ax =0; ax<FinalScale; ax+=spreadingFactor){

                for (int ay =0; ay<FinalScale; ay+=spreadingFactor){

                    for (int bx =0; bx<spreadingFactor; bx++){

                        for (int by = 0; by<spreadingFactor; by++){

                            Map[i][ax+bx][ay+by]=CurrentAmp*originalMap.GetNoise((float)(ax+bx),(float)(ay+by));
                            if (last){
                                float sum = 0f;

                                    for (int a = 0; a <Octaves; a++){
                                        sum+=Map[a][ax+bx][ay+by];
                                     }

                                        Map[i][ax+bx][ay+by]=sum/2f; 
                            }
                        }
                    }  
                }
            }

            CurrentAmp*=FractalGain;
            CurrentScale*=IterativeFactor;

        }

        float[][] FinalMap = new float[width][height];

        for (int x = 0; x<width; x++){

            for (int y =0; y<height; y++){
                FinalMap[x][y]=Map[0][x][y];
            }
        }
        Map=null;
        System.gc();

        return FinalMap;
    }
}
