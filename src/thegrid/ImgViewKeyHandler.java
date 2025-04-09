package thegrid;

import Catalano.Imaging.FastBitmap;
import Catalano.Imaging.Filters.Artistic.HeatMap;
import Catalano.Imaging.Filters.Artistic.OilPainting;
import Catalano.Imaging.Filters.Artistic.SpecularBloom;
import Catalano.Imaging.Filters.*;
import com.jhlabs.image.ContrastFilter;
import com.jhlabs.image.DiffuseFilter;
import com.jhlabs.image.HSBAdjustFilter;
import common.*;
import database.AccessCounter;
import database.DBHandler;
import dev.brachtendorf.jimagehash.hash.Hash;
import dev.brachtendorf.jimagehash.hashAlgorithms.HashingAlgorithm;
import dev.brachtendorf.jimagehash.hashAlgorithms.PerceptiveHash;
import dialogs.LineInput;
import dialogs.RGBScroll;
import dialogs.SliderBox;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

class ImgViewKeyHandler extends KeyAdapter {
    private final ImageView imageView;
    Timer timer = null;
    private long keyTime;

    public ImgViewKeyHandler(ImageView imageView) {
        this.imageView = imageView;
    }

    void stopTimer() {
        if (timer != null && timer.isRunning()) {
            timer.stop();
        }
    }

    private boolean slowDownKeyEvents() {
        long t = System.currentTimeMillis();
        long diff = t - keyTime;
        if (diff < 300)
            return false;
        else
            keyTime = t;
        return true;
    }

    public void keyPressed(KeyEvent e) {
        int ev = e.getKeyCode();
        switch (ev) {
            case KeyEvent.VK_UP -> {
                imageView.imgPanel.scrollDown();
                return;
            }
            case KeyEvent.VK_DOWN -> {
                imageView.imgPanel.scrollUp();
                return;
            }
            case KeyEvent.VK_LEFT -> {
                imageView.imgPanel.scrollRight();
                return;
            }
            case KeyEvent.VK_RIGHT -> {
                imageView.imgPanel.scrollLeft();
                return;
            }
            case KeyEvent.VK_CONTROL -> {
                return;
            }
        }
        if (!slowDownKeyEvents())
            return;
        switch (ev) {
            case KeyEvent.VK_PAGE_DOWN -> imageView.setNextImage();
            case KeyEvent.VK_PAGE_UP -> imageView.setBeforeImage();
            case KeyEvent.VK_PLUS -> {
                double factor = 1.5f;
                Point2D.Double p = imageView.scaleIconImg(factor, true);
                imageView.imgPanel.center(p);
            }
            case KeyEvent.VK_MINUS -> {
                double factor = 1.5f;
                Point2D.Double p = imageView.scaleIconImg(factor, false);
                imageView.imgPanel.center(p);
            }

            case KeyEvent.VK_J -> {
                int id = imageView.grid.imageL.get(imageView.indexRing.get()).rowid();
                String init = "" + AccessCounter.getAccCounter(id);
                int res = LineInput.onlyPosNumber(init, "new acc counter for: " + id,
                        Color.orange);
                AccessCounter.setAccCounter(id, res);
            }

            case KeyEvent.VK_R -> {
                BufferedImage img = imageView.getIconImg();
                img = ImgTools.rotateClockwise90(img);
                imageView.imgPanel.setImage(img);
                imageView.imgPanel.clearOffset();
            }

            case KeyEvent.VK_M -> { // mirror
                BufferedImage img = imageView.getIconImg();
                img = ImgTools.flip(img);
                imageView.imgPanel.setImage(img);
            }

            case KeyEvent.VK_W -> { // adjust on width
                imageView.imgPanel.clearOffset();
                imageView.adjustOn('w');
            }

            case KeyEvent.VK_T -> { // next img
                imageView.indexRing.set(imageView.shuffledRing.getNext());
                imageView.setImg();
                imageView.imgPanel.clearOffset();
                imageView.adjustOn('h');
            }

            case KeyEvent.VK_Z -> { // prev img, ctrlZ -> undo
                if (e.isControlDown()) {
                    imageView.imgPanel.undo();
                    return;
                }
                imageView.indexRing.set(imageView.shuffledRing.getPrev());
                imageView.setImg();
                imageView.imgPanel.clearOffset();
                imageView.adjustOn('h');
            }

            case KeyEvent.VK_S -> { // slideshow
                if (timer == null) {
                    timer = new Timer(10000, _ -> {
                        imageView.indexRing.set(imageView.shuffledRing.getNext());
                        imageView.setImg();
                        imageView.imgPanel.clearOffset();
                        imageView.adjustOn('h');
                    });
                    timer.setRepeats(true);
                    timer.setInitialDelay(0);
                    timer.start();
                } else {
                    timer.stop();
                    timer = null;
                    imageView.setTitle("Slideshow STOPPED " + imageView);
                }
            }

            case KeyEvent.VK_1 -> { // gamma
                BufferedImage img = imageView.getIconImg();
                float gamma = SliderBox.xmain("Gamma",
                        0.4f, 2.0f, 255);
                img = ImgTools.gammaCorrection(img, gamma);
                imageView.imgPanel.setImage(img);
            }

            case KeyEvent.VK_2 -> { // contrast correction
                FastBitmap fb = imageView.getIconAsFastBitmap();
                int factor = (int)SliderBox.xmain("Contrast",
                        -127f, 127f, 255);
                ContrastCorrection cc = new ContrastCorrection(factor);
                cc.applyInPlace(fb);
                imageView.imgPanel.setImage(fb);
            }

            case KeyEvent.VK_P -> { // special effect
                BufferedImage img = imageView.getIconImg();
                RGBScroll.xmain(img, imageView.imgPanel);
            }

            case KeyEvent.VK_D -> { // delete
                if (MsgBox.Question("Delete image from DB?")) {
                    DBHandler.deleteImage(imageView.grid.imageL.get(imageView.indexRing.get()).rowid());
                }
            }

            case KeyEvent.VK_H -> {
                imageView.imgPanel.clearOffset();
                imageView.adjustOn('h');
            }

            case KeyEvent.VK_3 -> {
                float factor = SliderBox.xmain("Luminance",
                        0.5f, 1.5f, 255);
                imageView.changeContrast(factor);
            }

            case KeyEvent.VK_4 -> {
                FastBitmap fb = imageView.getIconAsFastBitmap();
                int factor = (int)SliderBox.xmain("BrightnessCorrection",
                        -255f, 255f, 255);
                BrightnessCorrection bc = new BrightnessCorrection(factor);
                bc.applyInPlace(fb);
                imageView.imgPanel.setImage(fb);
            }

            case KeyEvent.VK_X -> imageView.sharpenImage();
            case KeyEvent.VK_F -> imageView.saveAsFile(true);
            case KeyEvent.VK_G -> imageView.saveAsFile(false);

            case KeyEvent.VK_L -> {
                imageView.imgPanel.clearOffset();
                imageView.setImg();
            }

            case KeyEvent.VK_ESCAPE -> imageView.dispose();

            case KeyEvent.VK_A -> {  // Tags
                int rowid = imageView.grid.imageL.get(imageView.indexRing.get()).rowid();
                String tag = LineInput.tagList(DBHandler.getTags(rowid), "Tag:", Color.YELLOW);
                DBHandler.setTag(rowid, tag);
            }

            case KeyEvent.VK_5 -> { // denoise
                BufferedImage img = imageView.getIconImg();
                Denoise d = new Denoise(img);
                BufferedImage out = d.perform_denoise();
                imageView.imgPanel.setImage(out);
            }

            case KeyEvent.VK_K -> { // +-10
                int this_rowid = imageView.grid.imageL.get(imageView.indexRing.get()).rowid();
                String sql = "select name,_ROWID_,tag,accnum from IMAGES where _rowid_ <= " +
                        (this_rowid+10) + " and _rowid_ >=" + (this_rowid-10);
                (new Thread(() -> new TheGrid(sql, "WORKER"))).start();
            }

            case KeyEvent.VK_F1 -> { // Grayscale
                FastBitmap fb = imageView.getIconAsFastBitmap();
                if (fb.isGrayscale())
                    return;
                fb.toGrayscale();
                imageView.imgPanel.setImage(fb);
            }

            case KeyEvent.VK_F2 -> { // Grayscale & forward fourier
                FastBitmap fb = imageView.getIconAsFastBitmap();
                if (!fb.isGrayscale())
                   fb.toGrayscale();
                FourierTransform ft = new FourierTransform(fb);
                ft.Forward();
                fb = ft.toFastBitmap();
                imageView.imgPanel.setImage(fb);
            }

            case KeyEvent.VK_F3 -> { // Diffuse
                BufferedImage img = imageView.getIconImg();
                DiffuseFilter d = new DiffuseFilter();
                BufferedImage ret = d.filter(img, null);
                imageView.imgPanel.setImage(ret);
            }

            case KeyEvent.VK_F4 -> { // HSB
                BufferedImage img = imageView.getIconImg();
                HSBAdjustFilter c = new HSBAdjustFilter(0.5f, 0.5f, 1.0f);
                BufferedImage ret = c.filter(img, null);
                imageView.imgPanel.setImage(ret);
            }


            case KeyEvent.VK_V -> { // similarities
                int this_rowid = imageView.grid.imageL.get(imageView.indexRing.get()).rowid();
                HashingAlgorithm hasher = new PerceptiveHash(32);
                Hash this_Hash = hasher.hash(imageView.getIconImg());
                ArrayList<DBHandler.HashId> hlist = DBHandler.loadPerceptiveImgHashes();
                HashSet<Integer> foundSet = new HashSet<>();
                for (DBHandler.HashId h : hlist) {
                    if (!h.hash().equals(this_Hash)) {
                        double similarityScore = this_Hash.normalizedHammingDistance(h.hash());
                        if (similarityScore < 0.2 && h.rowID() != this_rowid) {
                            foundSet.add(h.rowID());
                        }
                    }
                }
                if (!foundSet.isEmpty()) {
                    String xx = Tools.buildQueryForGrid(foundSet);
                    (new Thread(() -> new TheGrid(xx, "WORKER"))).start();
                } else {
                    MsgBox.Info("No similarities found!");
                }
            }

            case KeyEvent.VK_6 -> {
                int rad = (int)SliderBox.xmain("Dilatation",
                        1f, 13f, 256);
                imageView.applyInplaceFilter(new Dilatation(rad));
            }

            case KeyEvent.VK_7 -> {
                int rad = (int)SliderBox.xmain("OilPainting",
                        1f, 20f, 256);
                imageView.applyInplaceFilter(new OilPainting(rad));
            }

            case KeyEvent.VK_8 -> {
                int rad = (int)SliderBox.xmain("Erosion",
                        1f, 20f, 256);
                imageView.applyInplaceFilter(new Erosion(rad));
            }

            case KeyEvent.VK_9 -> {
                int r = (int)SliderBox.xmain("SpecularBloom",1f,20f,255);
                imageView.applyInplaceFilter(new SpecularBloom(20, r));
            }
            case KeyEvent.VK_0 -> imageView.applyInplaceFilter(new HistogramEqualization());
            case KeyEvent.VK_B -> {
                int r = (int)SliderBox.xmain("FastVariance",1f,20f,255);
                imageView.applyInplaceFilter(new FastVariance(r));
            }

            case KeyEvent.VK_Y -> {   //  heatmap
                FastBitmap fb = imageView.getIconAsFastBitmap();
                boolean inv = SliderBox.xmain("Heatmap",0f,1f,2) == 1f;
                HeatMap bl = new HeatMap(inv);
                bl.applyInPlace(fb);
                imageView.imgPanel.setImage(fb);
            }

            case KeyEvent.VK_N -> imageView.selectAnotherImage(-1);

            case KeyEvent.VK_C -> {
                if (e.isControlDown()) {
                    BufferedImage img = imageView.getIconImg();
                    ImgTools.imageToClipboard(img);
                } else {
                    int id = imageView.grid.imageL.get(imageView.indexRing.get()).rowid();
                    if (MsgBox.Question("Replace image #" + id)) {
                        BufferedImage img = imageView.getIconImg();
                        DBHandler.changeBigImg(img, id);
                    }
                }
            }

            case KeyEvent.VK_I -> {
                String name = "?";
                name = LineInput.xmain(name, "New Entry:", Color.RED);
                if (name.equals("?") || name.isEmpty())
                    return;
                BufferedImage img = imageView.getIconImg();
                try {
                    DBHandler.insertImageRecord(name, img);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }

            default -> Sam.speak("Key not used.");
        }
    }
}

/////////////////////////////////////////

