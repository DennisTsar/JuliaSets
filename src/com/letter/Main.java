package com.letter;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.*;

public class Main {
    public static void main(String[] args) {
        new JuliaSetProgram();
    }
}
class JuliaSetProgram extends JPanel implements AdjustmentListener, MouseListener{
    JFrame frame;
    int w = 700;
    int h = 400;
    int pixelSize = 1;
    JScrollBar aBar, bBar, zoomBar, hueBar, satBar, briBar;
    JPanel scrollPanel, labelPanel, buttonPanel, bigPanel;
    JLabel aLabel, bLabel, zoomLabel, hueLabel, satLabel, briLabel;
    BufferedImage image;
    JFileChooser fileChooser;
    public JuliaSetProgram(){
        frame = new JFrame("Julia Set Program");
        frame.add(this);
        frame.setSize(w,h);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        aBar = new JScrollBar(JScrollBar.HORIZONTAL,0,0,-2000,2000);
        bBar = new JScrollBar(JScrollBar.HORIZONTAL,0,0,-2000,2000);
        zoomBar = new JScrollBar(JScrollBar.HORIZONTAL,10,0,0,100);
        hueBar = new JScrollBar(JScrollBar.HORIZONTAL,0,0,0,1000);
        satBar = new JScrollBar(JScrollBar.HORIZONTAL,1000,0,0,1000);
        briBar = new JScrollBar(JScrollBar.HORIZONTAL,1000,0,0,1000);

        GridLayout  grid = new GridLayout(6,1);
        labelPanel = new JPanel();
        labelPanel.setLayout(grid);

        aLabel = new JLabel("A: 0\t\t");
        bLabel = new JLabel("B: 0\t\t");
        zoomLabel = new JLabel("Zoom: 1\t\t");
        hueLabel = new JLabel("Hue: 0\t\t");
        satLabel = new JLabel("Saturation: 1\t\t");
        briLabel = new JLabel("Brightness: 1\t\t");
        labelPanel.add(aLabel);
        labelPanel.add(bLabel);
        labelPanel.add(zoomLabel);
        labelPanel.add(hueLabel);
        labelPanel.add(satLabel);
        labelPanel.add(briLabel);

        scrollPanel = new JPanel();
        scrollPanel.setLayout(grid);
        scrollPanel.add(aBar);
        scrollPanel.add(bBar);
        scrollPanel.add(zoomBar);
        scrollPanel.add(hueBar);
        scrollPanel.add(satBar);
        scrollPanel.add(briBar);

        for(Component c : scrollPanel.getComponents()){
            ((JScrollBar)c).addAdjustmentListener(this);
            c.addMouseListener(this);
        }

        JButton clear=new JButton("Clear");
        clear.addActionListener(e -> {
            aBar.setValue(0);
            bBar.setValue(0);
            zoomBar.setValue(10);
            hueBar.setValue(0);
            satBar.setValue(1000);
            briBar.setValue(1000);
        });
        JButton save=new JButton("Save");
        fileChooser=new JFileChooser(System.getProperty("user.dir"));
        save.addActionListener(a -> {
            if(image!=null){
                FileFilter filter=new FileNameExtensionFilter("*.png","png");
                fileChooser.setFileFilter(filter);
                if(fileChooser.showSaveDialog(null)==JFileChooser.APPROVE_OPTION) {
                    File file=fileChooser.getSelectedFile();
                    try {
                        String st=file.getAbsolutePath();
                        if(st.contains(".png"))
                            st=st.substring(0,st.length()-4);
                        ImageIO.write(image,"png",new File(st+".png"));
                    }
                    catch(IOException e) {
                        System.out.println(e+"");
                    }
                }
            }
        });
        buttonPanel = new JPanel();
        buttonPanel.setLayout(grid);
        buttonPanel.add(save);
        buttonPanel.add(clear);

        bigPanel = new JPanel();
        bigPanel.setLayout(new BorderLayout());
        bigPanel.add(labelPanel,BorderLayout.WEST);
        bigPanel.add(scrollPanel,BorderLayout.CENTER);
        bigPanel.add(buttonPanel,BorderLayout.EAST);

        frame.add(bigPanel,BorderLayout.SOUTH);

        DrawJulia();

        frame.setVisible(true);
    }
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        g.drawImage(image,0,0,null);
    }
    public void adjustmentValueChanged(AdjustmentEvent e) {
        if(e.getSource()==aBar)
            aLabel.setText("A: "+aBar.getValue()/1000.0+"\t\t");
        else if(e.getSource()==bBar)
            bLabel.setText("A: "+bBar.getValue()/1000.0+"\t\t");
        else if(e.getSource()==zoomBar)
            zoomLabel.setText("Zoom: "+zoomBar.getValue()/10.0+"\t\t");
        else if(e.getSource()==hueBar)
            hueLabel.setText("Hue: "+hueBar.getValue()/1000.0+"\t\t");
        else if(e.getSource()==satBar)
            satLabel.setText("Saturation: "+satBar.getValue()/1000.0+"\t\t");
        else if(e.getSource()==briBar)
            briLabel.setText("Brightness: "+briBar.getValue()/1000.0+"\t\t");
        DrawJulia();
    }
    public void DrawJulia(){
        image = new BufferedImage(w,h,BufferedImage.TYPE_INT_RGB);

        for(int i = 0; i<w; i+=pixelSize)
            for(int j = 0; j<h; j+=pixelSize){
                float iter = 300f;
                double zx = 1.5*(i-.5*w)/(.5*zoomBar.getValue()/10.0*w);
                double zy = (j-h*.5)/(.5*zoomBar.getValue()/10.0*h);
                while(zx*zx+zy*zy<6 && iter>0){
                    double temp = zx*zx-zy*zy+aBar.getValue()/1000.0;
                    zy = 2*zx*zy + bBar.getValue()/1000.0;
                    zx = temp;
                    iter--;
                }
                int c;
                if(iter>0)
                    c = Color.HSBtoRGB(hueBar.getValue()/1000f*(iter/300) % 1,satBar.getValue()/1000f,briBar.getValue()/1000f) ;
                else
                    c = Color.HSBtoRGB(iter/300f,1,0);
                image.setRGB(i,j,c);
            }
        repaint();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        pixelSize = 2;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        pixelSize = 5;
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        pixelSize = 1;
        DrawJulia();
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}
