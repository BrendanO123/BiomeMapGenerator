import java.util.*;

//class for 2D noise
class Perlin_2D{

    ArrayList<Double> value=new ArrayList<>();
    int length;
    int width;
    int[] permuteTable = this.permuteTable();

    public Perlin_2D(int starting_length, int starting_width, int totalSmoothingFactor, 
    byte smoothing_iterations){

        length=starting_length;
        width=starting_width;

        //get gradient vectors
        for (int i=0;i<(length*width);i++){
            int index =2*permuteTable[i&255];
            value.add(Project.vector_table[index]);
            value.add(Project.vector_table[index+1]);
        }
        System.out.println(value.toString());

        for (int i=0; i<smoothing_iterations; i++){
            this.Smooth(totalSmoothingFactor/smoothing_iterations);
        }
        this.interpolate();
        System.out.println("Perlin static constructor 2D");
    }



     //permute table for indexes of gradient vectors list
     int[] permuteTable(){
        int[] value= Project.value.clone();
        //value.shuffle()
        for (int i=0; i<256; i++){
            Random x = new Random();
            int index=x.nextInt(256);
            int temp =value[index];
            value[index]=value[i];
            value[i]=temp;
        }
        return value;
        
    }



    
    //pre-generate equal spaced normalized 2D gradient vectors 
    static double[] VectorTable(){

        double[] end = new double[512];
        int[] value= Project.value.clone();

        //map value to fraction between [0,1)
        //map value to radian, [0,2Pi)
        //get x and y values for points on circle
        for (int i =0; i<512; i+=2){
            end[i]=(Math.sin((value[i/2]/(double)256)*(2*Math.PI)));
            end[i+1]=(Math.cos((value[i/2]/(double)256)*(2*Math.PI)));
        }
        return end;
    }

    void Smooth(int smoothingFactor){
        
    }

    void interpolate(){
        
    }
}