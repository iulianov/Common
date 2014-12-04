package aohara.common.content;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

public class ImageManager {
	
	private final String imageClassPath;
	private final Cache<BufferedImage> cache = new Cache<>();
	
	public ImageManager(){
		this("");
	}
	
	public ImageManager(String imageClassPath){
		this.imageClassPath = imageClassPath;
	}
	
	//-- Accessors ----------------------------------------------------------
	
	public BufferedImage getImage(String imageName){
		String path = imageClassPath + imageName;
		BufferedImage image = cache.get(path);
		if (image == null){
			URL url = ImageManager.class.getClassLoader().getResource(path);
			if (url != null){
				try {
					image = ImageIO.read(url);
				} catch (IOException e) {}
			}
			if (image != null){
				cache.put(image, path);
			} else {
				throw new RuntimeException("Unable to load image: " + path);
			}
		}
		return image;
	}
	
	public BufferedImage getImage(Path path) throws IOException{
		return ImageIO.read(path.toFile());
	}
	
	public ImageIcon getIcon(String imageName){
		return new ImageIcon(getImage(imageName));
	}
	
	public BufferedImage resizeImage(BufferedImage original, Dimension newDim){
		BufferedImage image = cache.get(original, newDim);
		if (image == null){
			image = new BufferedImage(newDim.width, newDim.height, original.getType());
			Graphics g = image.createGraphics();
			g.drawImage(original, 0, 0, newDim.width, newDim.height, null);
			g.dispose();
			
			cache.put(original, newDim, image);
		}
		return image;
	}
	
	public Dimension scaleToFit(BufferedImage image, Dimension toFit) {
	    double dScale = 1d;
	    if (image != null && toFit != null) {
	        double dScaleWidth = getScaleFactor(image.getWidth(), toFit.width);
	        double dScaleHeight = getScaleFactor(image.getHeight(), toFit.height);
	        dScale = Math.min(dScaleHeight, dScaleWidth);
	        return new Dimension(
		    	(int) Math.round(image.getWidth() * dScale),
		    	(int) Math.round(image.getHeight() * dScale)
		    );
	    }
	    throw new RuntimeException("invalid arguments");
	}
	
	private double getScaleFactor(int iMasterSize, int iTargetSize) {
		return (double) iTargetSize / (double) iMasterSize;
	}
	
	public BufferedImage grayScale(BufferedImage original) {
		BufferedImage image = cache.get(original);
		if (image == null){
			image = new BufferedImage(
				original.getWidth(),original.getHeight(),
				BufferedImage.TYPE_INT_ARGB
			);

			for (int x = 0; x < original.getWidth(); x++) {
				for (int y = 0; y < original.getHeight(); y++) {
					int argb = original.getRGB(x, y);

					int a = (argb >> 24) & 0xff;
					int r = (argb >> 16) & 0xff;
					int g = (argb >> 8) & 0xff;
					int b = (argb) & 0xff;

					int l = (int) (.299 * r + .587 * g + .114 * b); // luminance

					image.setRGB(x, y, (a << 24) + (l << 16) + (l << 8) + l);
				}
			}
			cache.put(original, image);
		}
		return image;
	}
	
	public BufferedImage getShade(Color color, double alpha){
		BufferedImage image = cache.get(color, alpha);
		if (image == null){
			if (alpha < 0 || alpha > 1){
				System.err.println("Invalid alpha.  Must be 0<a<1.  Default to 0.5");
				alpha = (float) 0.5;
			}
			
			image = new BufferedImage(10, 10, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g2d = image.createGraphics();
			g2d.setColor(color);
			g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) alpha));
			g2d.fillRect(0, 0, 10, 10);
			cache.put(image, color, alpha);
		}
		return image;
	}
	
	public BufferedImage colorize(BufferedImage image, Color colour) {
        int width = image.getWidth();
        int height = image.getHeight();
        WritableRaster raster = image.getRaster();

        for (int xx = 0; xx < width; xx++) {
            for (int yy = 0; yy < height; yy++) {
                int[] pixels = raster.getPixel(xx, yy, (int[]) null);
                pixels[0] = colour.getRed();
                pixels[1] = colour.getGreen();
                pixels[2] = colour.getBlue();
                raster.setPixel(xx, yy, pixels);
            }
        }
        return image;
    }
}