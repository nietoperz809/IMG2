package thegrid;

import Catalano.Imaging.FastBitmap;
import Catalano.Imaging.Filters.Artistic.HeatMap;
import Catalano.Imaging.Filters.Artistic.OilPainting;
import Catalano.Imaging.Filters.Artistic.SpecularBloom;
import Catalano.Imaging.Filters.*;
import com.jhlabs.image.*;
import common.*;
import database.AccessCounter;
import database.DBHandler;
import dev.brachtendorf.jimagehash.hash.Hash;
import dev.brachtendorf.jimagehash.hashAlgorithms.HashingAlgorithm;
import dev.brachtendorf.jimagehash.hashAlgorithms.PerceptiveHash;
import dialogs.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashSet;

import static java.awt.event.KeyEvent.*;

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
            case VK_UP -> {
                imageView.imgPanel.scrollDown();
                return;
            }
            case VK_DOWN -> {
                imageView.imgPanel.scrollUp();
                return;
            }
            case VK_LEFT -> {
                imageView.imgPanel.scrollRight();
                return;
            }
            case VK_RIGHT -> {
                imageView.imgPanel.scrollLeft();
                return;
            }
            case VK_CONTROL -> {
                return;
            }
        }
        if (!slowDownKeyEvents())
            return;
        switch (ev) {
            case VK_PAGE_DOWN -> imageView.setNextImage();
            case VK_PAGE_UP -> imageView.setBeforeImage();
            case VK_PLUS -> {
                Point2D.Double p = imageView.scaleIconImg(true);
                imageView.imgPanel.center(p);
            }
            case VK_MINUS -> {
                Point2D.Double p = imageView.scaleIconImg(false);
                imageView.imgPanel.center(p);
            }

            case VK_J -> {
                int id = imageView.grid.imageL.get(imageView.indexRing.get()).rowid();
                String init = "" + AccessCounter.getAccCounter(id);
                int res = LineInput.onlyPosNumber(init, "new acc counter for: " + id,
                        Color.orange);
                AccessCounter.setAccCounter(id, res);
            }

            case VK_R -> {
                BufferedImage img = imageView.getIconImg();
                img = ImageTools.rotateClockwise90(img);
                imageView.imgPanel.setImageCentered(img);
            }

            case VK_M -> { // mirror
                BufferedImage img = imageView.getIconImg();
                img = ImageTools.flip(img);
                imageView.imgPanel.setImageCentered(img);
            }

            case VK_W -> { // adjust on width
                imageView.imgPanel.clearOffset();
                imageView.adjustOn('w');
            }

            case VK_T -> { // next img
                imageView.indexRing.set(imageView.shuffledRing.getNext());
                imageView.setImg();
                imageView.imgPanel.clearOffset();
                imageView.adjustOn('h');
            }

            case VK_Z -> { // prev img, ctrlZ -> undo
                if (e.isControlDown()) {
                    imageView.imgPanel.undo();
                    return;
                }
                imageView.indexRing.set(imageView.shuffledRing.getPrev());
                imageView.setImg();
                imageView.imgPanel.clearOffset();
                imageView.adjustOn('h');
            }

            case VK_S -> { // slideshow
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

            case VK_P -> { // HSBAdjustFilter
                BufferedImage img = imageView.getIconImg();
                RGBScroll.xmain(img, imageView.imgPanel);
            }

            case VK_1 -> { // emboss
                BufferedImage img = imageView.getIconImg();
                EmbossFilter emb = new EmbossFilter();
                BufferedImage out = emb.filter(img, null);
                imageView.imgPanel.setImage(out);
            }

            case VK_2 -> { // contrast, brightness
                BufferedImage img = imageView.getIconImg();
                ConBright.xmain(img, imageView.imgPanel);
            }

            case VK_3 -> {
                BufferedImage img = imageView.getIconImg();
                MultiSlider.xmain(img, imageView.imgPanel);
            }

            case VK_D -> { // delete
                if (MsgBox.Question("Delete image from DB?")) {
                    DBHandler.deleteImage(imageView.grid.imageL.get(imageView.indexRing.get()).rowid());
                }
            }

            case VK_H -> {
                imageView.imgPanel.clearOffset();
                imageView.adjustOn('h');
            }

//            case VK_3 -> {
//                float factor = SliderBox.xmain("Luminance",
//                        0.5f, 1.5f, 255);
//                imageView.changeContrast(factor);
//            }

            case VK_4 -> {
                FastBitmap fb = imageView.getIconAsFastBitmap();
                int factor = (int) SliderBox.xmain("BrightnessCorrection",
                        -255f, 255f, 255);
                BrightnessCorrection bc = new BrightnessCorrection(factor);
                bc.applyInPlace(fb);
                imageView.imgPanel.setImage(fb);
            }

            case VK_X -> {
                BufferedImage img = imageView.getIconImg();
                BufferedImage out = ImageTools.sharpenImage(img, !e.isControlDown());
                imageView.imgPanel.setImage(out);
            }

            case VK_F -> imageView.saveAsFile(true);
            case VK_G -> imageView.saveAsFile(false);

            case VK_L -> {
                imageView.imgPanel.clearOffset();
                imageView.setImg();
            }

            case VK_ESCAPE -> imageView.dispose();

            case VK_A -> {  // Tags
                int rowid = imageView.grid.imageL.get(imageView.indexRing.get()).rowid();
                String tag = LineInput.tagList(DBHandler.getTags(rowid), "Tag:", Color.YELLOW);
                DBHandler.setTag(rowid, tag);
            }

            case VK_5 -> { // denoise
                BufferedImage img = imageView.getIconImg();
                Denoise d = new Denoise(img);
                BufferedImage out = d.perform_denoise();
                imageView.imgPanel.setImage(out);
            }

            case VK_K -> { // +-10
                int this_rowid = imageView.grid.imageL.get(imageView.indexRing.get()).rowid();
                String sql = "select name,_ROWID_,tag,accnum from IMAGES where _rowid_ <= " +
                        (this_rowid + 10) + " and _rowid_ >=" + (this_rowid - 10);
                (new Thread(() -> new TheGrid(sql, "WORKER"))).start();
            }

            case VK_F1 -> { // Grayscale
                FastBitmap fb = imageView.getIconAsFastBitmap();
                if (fb.isGrayscale())
                    return;
                fb.toGrayscale();
                imageView.imgPanel.setImage(fb);
            }

            case VK_F2 -> { // Grayscale & forward fourier
                FastBitmap fb = imageView.getIconAsFastBitmap();
                if (!fb.isGrayscale())
                    fb.toGrayscale();
                FourierTransform ft = new FourierTransform(fb);
                ft.Forward();
                fb = ft.toFastBitmap();
                imageView.imgPanel.setImage(fb);
            }

            case VK_F3 -> { // Diffuse
                BufferedImage img = imageView.getIconImg();
                DiffuseFilter d = new DiffuseFilter();
                BufferedImage ret = d.filter(img, null);
                imageView.imgPanel.setImage(ret);
            }

            case VK_F4 -> { // Equalize
                BufferedImage img = imageView.getIconImg();
                EqualizeFilter c = new EqualizeFilter();
                BufferedImage ret = c.filter(img, null);
                imageView.imgPanel.setImage(ret);
            }

            case VK_F5 -> { // Skeleton
                BufferedImage img = imageView.getIconImg();
                SkeletonFilter c = new SkeletonFilter();
                BufferedImage ret = c.filter(img, null);
                imageView.imgPanel.setImage(ret);
            }

            case VK_I -> { // Invert
                BufferedImage img = imageView.getIconImg();
                InvertFilter d = new InvertFilter();
                BufferedImage ret = d.filter(img, null);
                imageView.imgPanel.setImage(ret);
            }

            case VK_V -> { // similarities
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

            case VK_6 -> {
                int rad = (int) SliderBox.xmain("Dilatation",
                        1f, 13f, 256);
                imageView.applyInplaceFilter(new Dilatation(rad));
            }

            case VK_7 -> {
                int rad = (int) SliderBox.xmain("OilPainting",
                        1f, 20f, 256);
                imageView.applyInplaceFilter(new OilPainting(rad));
            }

            case VK_8 -> {
                int rad = (int) SliderBox.xmain("Erosion",
                        1f, 20f, 256);
                imageView.applyInplaceFilter(new Erosion(rad));
            }

            case VK_9 -> {
                int r = (int) SliderBox.xmain("SpecularBloom", 1f, 20f, 255);
                imageView.applyInplaceFilter(new SpecularBloom(20, r));
            }
            case VK_0 -> imageView.applyInplaceFilter(new HistogramEqualization());
            case VK_B -> {
                int r = (int) SliderBox.xmain("FastVariance", 1f, 20f, 255);
                imageView.applyInplaceFilter(new FastVariance(r));
            }

            case VK_Y -> {   //  heatmap
                FastBitmap fb = imageView.getIconAsFastBitmap();
                boolean inv = SliderBox.xmain("Heatmap", 0f, 1f, 2) == 1f;
                HeatMap bl = new HeatMap(inv);
                bl.applyInPlace(fb);
                imageView.imgPanel.setImage(fb);
            }

            case VK_N -> imageView.selectAnotherImage(-1);

            case VK_C -> {
                if (e.isControlDown()) {
                    BufferedImage img = imageView.getIconImg();
                    ImageTools.imageToClipboard(img);
                } else {
                    int id = imageView.grid.imageL.get(imageView.indexRing.get()).rowid();
                    if (MsgBox.Question("Replace image #" + id)) {
                        BufferedImage img = imageView.getIconImg();
                        DBHandler.changeBigImg(img, id);
                    }
                }
            }

//            case VK_I -> {
//                String name = "?";
//                name = LineInput.xmain(name, "New Entry:", Color.RED);
//                if (name.equals("?") || name.isEmpty())
//                    return;
//                BufferedImage img = imageView.getIconImg();
//                try {
//                    DBHandler.insertImageRecord(name, img);
//                } catch (IOException ex) {
//                    throw new RuntimeException(ex);
//                }
//            }

            default -> Sam.speak("Key not used.");
        }
        Audio.playWaveFromResource("drop.wav");
    }
}

/////////////////////////////////////////

