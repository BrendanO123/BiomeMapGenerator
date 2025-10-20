import java.awt.*;
import java.awt.image.*;
import java.io.*;
import javax.imageio.*;

class TextWriter {
    private Graphics2D Writer;
    private BufferedImage image;
    private int Size=24;
    private int Gap_Distance=2;
    public TextWriter(BufferedImage img){
        Writer= img.createGraphics();
        image = img;
        Writer.setFont(new Font("Text", Font.PLAIN, 24));
        //Writer.setColor(Color.BLACK);
    }
    public void setFontSize(int size){
        Writer.setFont(new Font("Text", Font.PLAIN, size));
        Size=size;
    }
    public void setGapSize(int size){
        Gap_Distance=size;
    }
    
    public void WriteKeyElement(int color, String name, int x, int y){
        for (byte x2=0; x2<6; x2++){
            for (byte y2=0; y2<Size; y2++){
                image.setRGB(x+x2, y-y2, color);
            }
        }
        Writer.drawString(name, x+6+Gap_Distance, y);
    }

    public void Close(){
        Writer.dispose();
        Writer=null;
    }
}

class Key{
    public static BufferedImage WriteKey(BufferedImage img, String[] biomes, int[] colors, int allocatedHeight, int Gap_size, int Font_Size){
        int width=img.getWidth();
        int UpperBound = img.getHeight()-allocatedHeight;
        int LowerBound = img.getHeight();


        int totalSpacing = Gap_size+ Font_Size;

        int count=biomes.length;

        int maxSize=0;
        for (int i =0; i<count; i++){
            if (biomes[i].length()>maxSize){maxSize=biomes[i].length();}
        }
        maxSize*=Font_Size;
        maxSize+=6+(Gap_size*3);

        //if ((width/maxSize)*(allocatedHeight/totalSpacing)<count){
                //System.err.println("Small Image Frame");
                if (allocatedHeight!=(count*totalSpacing*maxSize)/width){
                    allocatedHeight=(count*totalSpacing*maxSize)/width;
                    LowerBound=UpperBound+allocatedHeight;
                    BufferedImage  Image=new BufferedImage(width, LowerBound, 1);
                    for (int x=0; x<width;x++){
                        for (int y=0; y<LowerBound;y++){
                            Image.setRGB(x, y, img.getRGB(x, y));
                        }
                    }
                    img = Image;
                    Image=null;
                    System.out.println(allocatedHeight);
                }
        //}

        TextWriter Writer = new TextWriter(img);

        if (Gap_size!=2){Writer.setGapSize(Gap_size);}
        if(Font_Size!=24){Writer.setFontSize(Font_Size);}

        count=0;
        placement : for (int x=0;x<width; x+=maxSize){
            for (int y=UpperBound+totalSpacing;y<LowerBound;y+=totalSpacing){
                if (count>=biomes.length){break placement;}
                Writer.WriteKeyElement(colors[count], biomes[count], x, y);
                count++;
            }
        }

        return img;
    }
}
class BiomeImage{
    public static void GetBiomeImage(Biomes[][] BiomeMap){
        short width = (short)BiomeMap.length;
        short length = (short)BiomeMap[0].length;

        BufferedImage BiomeImage = new BufferedImage(2048, 2048+288, 1);

        //Don't let unstable touch shallow ocean

        //Too Much frozen ocean

        //Potentially to much volcano, it is not that common to even get one but then 
        //when you do it can be way to big, might be fixed with more noise layers
        //Might Need more bias towards snowy mountains

        //shallow ocean line too hard
        //same with inland, overlay with noise, restrict noise shift to unilateral



        //island forms and then is cut off when meeting shallow ocean
        //removed additional island generation

        //islands formed by low erosion have too much shore or non when cut off by shallow ocean
        //removed additional island generation

        //Potentially too much spread of shallow ocean from low erosion
        //removed additional shallow ocean from low erosion generation

        for (short x=0; x<width; x++){
            iteration : for (short y=0; y<length; y++){
                switch (BiomeMap[x][y]){
                    case Shore:
                        BiomeImage.setRGB(x,y, Color.YELLOW.getRGB());
                        break;
                    case ShallowOcean:
                        BiomeImage.setRGB(x,y, new Color(30,60,255).getRGB());
                        break;
                    /*case DeepOcean:
                        BiomeImage.setRGB(x,y, new Color(0,0,230).getRGB());
                        break;
                    case LowLand:
                        BiomeImage.setRGB(x,y, new Color(138, 141, 40).getRGB());
                        break;
                    case Inland:
                        BiomeImage.setRGB(x,y, new Color(34, 139, 34).getRGB());
                        break;/* */
                    case Desert:
                        BiomeImage.setRGB(x,y, new Color(255, 230, 0).getRGB());
                        break;
                    case Swamp:
                        BiomeImage.setRGB(x,y,  new Color(150, 75, 0).getRGB());
                        break;
                    case Frozen_Ocean:
                        BiomeImage.setRGB(x,y, new Color(150, 221, 250).getRGB());
                        break;
                    case Cold_Ocean:
                        BiomeImage.setRGB(x,y, new Color(135, 206, 235).getRGB());
                        break;
                    case Warm_Ocean:
                        BiomeImage.setRGB(x,y, new Color(0,0,230).getRGB());
                        break;
                    case Tropic_Ocean:
                        BiomeImage.setRGB(x,y, new Color(75,0,240).getRGB());
                        break;
                    case Unstable_Ocean:
                        BiomeImage.setRGB(x,y, new Color(120, 0, 220).getRGB());
                        break;
                    case Frozen_Shallows:
                        BiomeImage.setRGB(x,y, new Color(203, 238, 253).getRGB());
                        break;
                    case Reef:
                        BiomeImage.setRGB(x,y, new Color(248, 131, 121).getRGB());
                        break;
                    case Snowy_Peaks:
                        BiomeImage.setRGB(x,y, Color.WHITE.getRGB());
                        break;
                    case Rocky_Peaks:
                        BiomeImage.setRGB(x,y, Color.GRAY.getRGB());
                        break;
                    case Volcano:
                        BiomeImage.setRGB(x,y, Color.RED.getRGB());
                        break;
                    case Hilly_Tundra:
                        BiomeImage.setRGB(x,y, new Color(170, 230, 145).getRGB());
                        break;
                    case Snowy_Forest_Hills:
                        BiomeImage.setRGB(x,y, new Color(144, 238, 144).getRGB());
                        break;
                    case Cold_Hilled_Forest:
                        BiomeImage.setRGB(x,y, new Color(138, 141, 40).getRGB());
                        break;
                    case Redwood_Forest_Hills:
                        BiomeImage.setRGB(x,y, new Color(168, 66, 45).getRGB());
                        break;
                    case Rolling_Sparse_Plains:
                        BiomeImage.setRGB(x,y, new Color(34, 139, 34).getRGB());
                        break;
                    case Tundra:
                        BiomeImage.setRGB(x,y, new Color(170, 230, 145).getRGB());
                        break;
                    case Snowy_Forest:
                        BiomeImage.setRGB(x,y, new Color(144, 238, 144).getRGB());
                        break;
                    case Cold_Forest:
                        BiomeImage.setRGB(x,y, new Color(138, 141, 40).getRGB());
                        break;
                    case Redwood_Forest:
                        BiomeImage.setRGB(x,y, new Color(168, 66, 45).getRGB());
                        break;
                    case Sparse_Plains:
                        BiomeImage.setRGB(x,y, new Color(34, 139, 34).getRGB());
                        break;
                    case Grassland:
                        BiomeImage.setRGB(x,y, new Color(13,152,186).getRGB());
                        break;
                    case Cold_Desert:
                        BiomeImage.setRGB(x,y, new Color(255,255,194).getRGB());
                        break;
                    case Rocky_Hills:
                        BiomeImage.setRGB(x,y, new Color(203,165,97).getRGB());
                        break;
                    case Dry_Forest_Hills:
                        BiomeImage.setRGB(x,y, new Color(127,112,83).getRGB());
                        break;
                    case Moderate_Forest_Hills:
                        BiomeImage.setRGB(x,y, new Color(129,165,49).getRGB());
                        break;
                    case Lush_Forest_Hills:
                        BiomeImage.setRGB(x,y, Color.PINK.getRGB());
                        break;
                    case Jungle_Hills:
                        BiomeImage.setRGB(x,y, new Color(55, 165, 0).getRGB());
                        break;
                    case Dry_Forest:
                        BiomeImage.setRGB(x,y, new Color(127,112,83).getRGB());
                        break;
                    case Moderate_Forest:
                        BiomeImage.setRGB(x,y, new Color(129,165,49).getRGB());
                        break;
                    case Lush_Forest:
                        BiomeImage.setRGB(x,y, Color.PINK.getRGB());
                        break;
                    case Jungle:
                        BiomeImage.setRGB(x,y, new Color(55, 165, 0).getRGB());
                        break;
                    case Prairie:
                        BiomeImage.setRGB(x,y, new Color(210,180,140).getRGB());
                        break;
                    case Rolling_Prairie:
                        BiomeImage.setRGB(x,y, new Color(210,180,140).getRGB());
                        break;
                    case Rolling_Grassland:
                        BiomeImage.setRGB(x,y, new Color(13,152,186).getRGB());
                        break;
                    case Snowy_Tundra:
                        BiomeImage.setRGB(x,y, new Color(171, 212, 192).getRGB());
                        break;
                    case Lush_Meadow:
                        BiomeImage.setRGB(x,y, new Color(208, 191, 202).getRGB());
                        break;
                    case Snowy_Meadow:
                        BiomeImage.setRGB(x,y, new Color(183, 238, 216).getRGB());
                        break;
                    case Rocky_Meadow:
                        BiomeImage.setRGB(x,y, new Color(161, 135, 49).getRGB());
                        break;
                    case Rocky_Desert:
                        BiomeImage.setRGB(x,y, new Color(220, 195, 65).getRGB());
                        break;
                    default:
                        System.err.println("Unexpected Biome: " + BiomeMap[x][y].name());
                        //BiomeImage.setRGB(x,y, new Color(0,0,230).getRGB());
                        continue iteration;
                }
            }
        }

        BiomeImage = Key.WriteKey(BiomeImage, 
        
        new String[]{"Shore", "Shallow Ocean", "Desert", "Swamp", "Frozen Ocean", 
        "Cold Ocean", "Warm Ocean", "Tropical Ocean", "Unstable Ocean", "Frozen Shallows", "Reef", "Snowy Peaks", "Rocky Peaks", 
        "Volcano", "Tundra*", "Snowy Forest*", "Cold Forest*", "Redwood Forest*", "Sparse Plains*", "Grasslands*", "Cold Dessert",
         "Rocky Hills", "Dry Forest*", "Moderate Forest*", "Lush Forest*", "Jungle*", "Prairie*", "Snowy Wasteland",
          "Snowy Meadow", "Lush Meadow", "Rocky Meadow", "Rocky Dessert"},

          new int[]{Color.YELLOW.getRGB(), new Color(30,60,255).getRGB(), new Color(255, 230, 0).getRGB(), 
            new Color(150, 75, 0).getRGB(), new Color(150, 221, 250).getRGB(), new Color(135, 206, 235).getRGB(),
            new Color(0,0,230).getRGB(), new Color(75,0,240).getRGB(), new Color(120, 0, 220).getRGB(), new Color(210, 200, 170).getRGB(),
            new Color(248, 131, 121).getRGB(), Color.WHITE.getRGB(), Color.GRAY.getRGB(), Color.RED.getRGB(), 
            new Color(170, 230, 145).getRGB(), new Color(144, 238, 144).getRGB(), new Color(138, 141, 40).getRGB(), 
            new Color(168, 66, 45).getRGB(), new Color(34, 139, 34).getRGB(), new Color(13,152,186).getRGB(), 
            new Color(255,255,194).getRGB(), new Color(203,165,97).getRGB(), new Color(127,112,83).getRGB(),
            new Color(129,165,49).getRGB(), Color.PINK.getRGB(), new Color(55, 165, 0).getRGB(), 
            new Color(210,180,140).getRGB(), new Color(171, 212, 192).getRGB(), new Color(183, 238, 216).getRGB(), 
            new Color(208, 191, 202).getRGB(), new Color(161, 135, 49).getRGB(), new Color(220, 195, 65).getRGB()},

           288, 3, 32);
        
        try{ImageIO.write(BiomeImage, "PNG", new File("Outputs/BiomeImage.png"));}
        catch (IOException e){e.printStackTrace();}

        BiomeImage=null;
    }
}