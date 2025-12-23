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
import database.ImageImport;
import dev.brachtendorf.jimagehash.hash.Hash;
import dev.brachtendorf.jimagehash.hashAlgorithms.HashingAlgorithm;
import dev.brachtendorf.jimagehash.hashAlgorithms.PerceptiveHash;
import dialogs.*;
import jfxapps.JfxImageView;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

import static common.Tools.newGridForSet;
import static java.awt.event.KeyEvent.*;

public class ImgViewKeyHandler extends KeyAdapter {
    private final ImageFrame imageFrame;
    private volatile boolean anyReleased = true;

    public ImgViewKeyHandler(ImageFrame imageFrame) {
        this.imageFrame = imageFrame;
    }

    public void keyPressed(KeyEvent e) {
        if (!anyReleased || e.getKeyCode() == VK_CONTROL ||
                e.getKeyCode() == VK_SHIFT)
            return;
        anyReleased = false;
        //Audio.playAsyncWave("myfirst.wav");

        doForKey(e);
    }

    @Override
    public void keyReleased(KeyEvent e) {
        anyReleased = true;  // suppress key repeat
        //Audio.playAsyncWave("mylast.wav");
    }

    private void doForKey (KeyEvent e) {
        switch (e.getKeyCode()) {
            case VK_ENTER -> {
                JfxImageView jiv = JfxImageView.create(imageFrame.imgPanel);
            }
            case VK_UP -> {
                imageFrame.imgPanel.scrollDown(imageFrame.imgPanel);
                anyReleased = true;
            }
            case VK_DOWN -> {
                imageFrame.imgPanel.scrollUp(imageFrame.imgPanel);
                anyReleased = true;
            }
            case VK_LEFT -> {
                imageFrame.imgPanel.scrollRight(imageFrame.imgPanel);
                anyReleased = true;
            }
            case VK_RIGHT -> {
                imageFrame.imgPanel.scrollLeft(imageFrame.imgPanel);
                anyReleased = true;
            }
            case VK_PAGE_DOWN -> imageFrame.setNextImage();
            case VK_PAGE_UP -> imageFrame.setPrevImage();
            case VK_PLUS -> {
                Point2D.Double p = imageFrame.scaleIconImg(true);
                imageFrame.imgPanel.center(imageFrame.imgPanel, p);
            }
            case VK_MINUS -> {
                Point2D.Double p = imageFrame.scaleIconImg(false);
                imageFrame.imgPanel.center(imageFrame.imgPanel,p);
            }

            case VK_J -> {
                int id = imageFrame.grid.imageL.get(imageFrame.indexRing.get()).rowid();
                String init = "" + AccessCounter.getAccCounter(id);
                int res = Input.getInteger("acc counter for: "+id, init);
                AccessCounter.setAccCounter(id, res);
            }

            case VK_Q -> { // make gray image
                BufferedImage img = imageFrame.getIconImg();
                img = ImageTools.toGray(img);
                imageFrame.imgPanel.setImageCentered(img);
            }

            case VK_R -> {
                BufferedImage img = imageFrame.getIconImg();
                img = ImageTools.rotateClockwise90(img);
                imageFrame.imgPanel.setImageCentered(img);
            }

            case VK_M -> { // mirror
                BufferedImage img = imageFrame.getIconImg();
                img = ImageTools.flip(img);
                imageFrame.imgPanel.setImageCentered(img);
            }

            case VK_W -> { // adjust on width
                imageFrame.imgPanel.clearOffset();
                imageFrame.adjustOn('w');
            }

            case VK_T -> { // next image
                imageFrame.indexRing.set(imageFrame.shuffledRing.getNext());
                imageFrame.setImg();
                imageFrame.imgPanel.clearOffset();
                imageFrame.adjustOn('h');
            }

            case VK_Z -> { // prev image, ctrlZ -> undo
                if (e.isControlDown()) {
                    imageFrame.imgPanel.undoImage();
                    return;
                }
                imageFrame.indexRing.set(imageFrame.shuffledRing.getPrev());
                imageFrame.setImg();
                imageFrame.imgPanel.clearOffset();
                imageFrame.adjustOn('h');
            }

            case VK_S -> { // Put copy into DB on CTRL-S
                if (e.isControlDown()) {
                    BufferedImage img = imageFrame.getIconImg();
                    try {
                        DBHandler.insertImageRecord (new ImageImport(ImageImport.Decoder.LOCALCOPY, img));
                        Sam.speak("copy inserted");
                    } catch (IOException _) {
                        Sam.speak("copy failed");
                    }
                }
            }

            case VK_P -> { // HSBAdjustFilter
                BufferedImage img = imageFrame.getIconImg();
                RGBScroll.xmain(img, imageFrame.imgPanel);
            }

            case VK_1 -> { // emboss
                BufferedImage img = imageFrame.getIconImg();
                EmbossFilter emb = new EmbossFilter();
                BufferedImage out = emb.filter(img, null);
                imageFrame.imgPanel.setImage(out);
            }

            case VK_2 -> { // contrast, brightness
                BufferedImage img = imageFrame.getIconImg();
                ConBright.xmain(img, imageFrame.imgPanel);
            }

            case VK_3 -> {
                BufferedImage img = imageFrame.getIconImg();
                MultiSlider.xmain(img, imageFrame.imgPanel);
            }

            case VK_D -> { // delete
                if (MsgBox.Question("Delete image from DB?")) {
                    DBHandler.deleteImage(imageFrame.grid.imageL.get(imageFrame.indexRing.get()).rowid());
                }
            }

            case VK_H -> {
                imageFrame.imgPanel.clearOffset();
                imageFrame.adjustOn('h');
            }

            case VK_4 -> {
                FastBitmap fb = imageFrame.getIconAsFastBitmap();
                int factor = (int) SliderBox.xmain("BrightnessCorrection",
                        -255f, 255f, 255);
                BrightnessCorrection bc = new BrightnessCorrection(factor);
                bc.applyInPlace(fb);
                imageFrame.imgPanel.setImage(fb);
            }

            case VK_X -> {
                BufferedImage img = imageFrame.getIconImg();
                BufferedImage out = ImageTools.sharpenImage(img, !e.isControlDown());
                imageFrame.imgPanel.setImage(out);
            }

            case VK_F -> imageFrame.saveAsFile(true);
            case VK_G -> imageFrame.saveAsFile(false);

            case VK_L -> {
                imageFrame.imgPanel.clearOffset();
                imageFrame.setImg();
            }

            case VK_ESCAPE -> imageFrame.dispose();

            case VK_A -> {  // Tags
                int rowid = imageFrame.grid.imageL.get(imageFrame.indexRing.get()).rowid();
                String tag = Tagger.tagList(DBHandler.getTags(rowid), "Tag:", Color.YELLOW);
                DBHandler.setTag(rowid, tag);
            }

            case VK_5 -> { // denoise
                BufferedImage img = imageFrame.getIconImg();
                Denoise d = new Denoise(img);
                BufferedImage out = d.perform_denoise();
                imageFrame.imgPanel.setImage(out);
            }

            case VK_K -> { // +-10
                int this_rowid = imageFrame.grid.imageL.get(imageFrame.indexRing.get()).rowid();
                String sql = "select name,_ROWID_,tag,accnum from IMAGES where _rowid_ <= " +
                        (this_rowid + 10) + " and _rowid_ >=" + (this_rowid - 10);
                (new Thread(() -> new TheGrid(sql, "WORKER"))).start();
            }

            case VK_F1 -> { // Grayscale
                FastBitmap fb = imageFrame.getIconAsFastBitmap();
                if (fb.isGrayscale())
                    return;
                fb.toGrayscale();
                imageFrame.imgPanel.setImage(fb);
            }

            case VK_F2 -> { // Grayscale & forward fourier
                FastBitmap fb = imageFrame.getIconAsFastBitmap();
                if (!fb.isGrayscale())
                    fb.toGrayscale();
                FourierTransform ft = new FourierTransform(fb);
                ft.Forward();
                fb = ft.toFastBitmap();
                imageFrame.imgPanel.setImage(fb);
            }

            case VK_F3 -> { // Diffuse
                BufferedImage img = imageFrame.getIconImg();
                DiffuseFilter d = new DiffuseFilter();
                BufferedImage ret = d.filter(img, null);
                imageFrame.imgPanel.setImage(ret);
            }

            case VK_F4 -> { // Equalize
                BufferedImage img = imageFrame.getIconImg();
                EqualizeFilter c = new EqualizeFilter();
                BufferedImage ret = c.filter(img, null);
                imageFrame.imgPanel.setImage(ret);
            }

            case VK_F5 -> { // Skeleton
                BufferedImage img = imageFrame.getIconImg();
                SkeletonFilter c = new SkeletonFilter();
                BufferedImage ret = c.filter(img, null);
                imageFrame.imgPanel.setImage(ret);
            }

            case VK_I -> { // Invert
                BufferedImage img = imageFrame.getIconImg();
                InvertFilter d = new InvertFilter();
                BufferedImage ret = d.filter(img, null);
                imageFrame.imgPanel.setImage(ret);
            }

            case VK_U -> { // one similar image
                int this_rowid = imageFrame.grid.imageL.get(imageFrame.indexRing.get()).rowid();
                HashingAlgorithm hasher = new PerceptiveHash(32);
                Hash this_Hash = hasher.hash(imageFrame.getIconImg());
                final HashSet<Integer> simi = getSimilarities(this_Hash, this_rowid);
                if(simi.isEmpty())
                    return;
                System.out.println(simi);
                ArrayList<Integer> li = new ArrayList<>(simi);
                Collections.shuffle(li);
                imageFrame.selectAnotherImage(li.getFirst());
            }

            case VK_V -> { // similarities
                int this_rowid = imageFrame.grid.imageL.get(imageFrame.indexRing.get()).rowid();
                HashingAlgorithm hasher = new PerceptiveHash(32);
                Hash this_Hash = hasher.hash(imageFrame.getIconImg());
                HashSet<Integer> set = loadSimilarities (this_Hash, this_rowid);
                System.out.println(set);
            }

            case VK_6 -> {
                int rad = (int) SliderBox.xmain("Dilatation",
                        1f, 13f, 256);
                imageFrame.applyInplaceFilter(new Dilatation(rad));
            }

            case VK_7 -> {
                int rad = (int) SliderBox.xmain("OilPainting",
                        1f, 20f, 256);
                imageFrame.applyInplaceFilter(new OilPainting(rad));
            }

            case VK_8 -> {
                int rad = (int) SliderBox.xmain("Erosion",
                        1f, 20f, 256);
                imageFrame.applyInplaceFilter(new Erosion(rad));
            }

            case VK_9 -> {
                int r = (int) SliderBox.xmain("SpecularBloom", 1f, 20f, 255);
                imageFrame.applyInplaceFilter(new SpecularBloom(20, r));
            }
            case VK_0 -> imageFrame.applyInplaceFilter(new HistogramEqualization());
            case VK_B -> {
                int r = (int) SliderBox.xmain("FastVariance", 1f, 20f, 255);
                imageFrame.applyInplaceFilter(new FastVariance(r));
            }

            case VK_Y -> {   //  heatmap
                FastBitmap fb = imageFrame.getIconAsFastBitmap();
                boolean inv = SliderBox.xmain("Heatmap", 0f, 1f, 2) == 1f;
                HeatMap bl = new HeatMap(inv);
                bl.applyInPlace(fb);
                imageFrame.imgPanel.setImage(fb);
            }

            case VK_N -> imageFrame.selectAnotherImage(-1);

            case VK_C -> {
                if (e.isControlDown()) {
                    BufferedImage img = imageFrame.getIconImg();
                    ImageTools.imageToClipboard(img);
                } else {
                    int id = imageFrame.grid.imageL.get(imageFrame.indexRing.get()).rowid();
                    if (MsgBox.Question("Replace image #" + id)) {
                        BufferedImage img = imageFrame.getIconImg();
                        DBHandler.changeBigImg(img, id);
                    }
                }
            }

        }
    }

    public static HashSet<Integer> getSimilarities(Hash this_Hash, int this_rowid) {
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
        if(foundSet.isEmpty()){
            MsgBox.Info("No similarities found!");
        }
        return foundSet;
    }

    public static HashSet<Integer> loadSimilarities(Hash this_Hash, int this_rowid) {
        HashSet<Integer> foundSet = getSimilarities(this_Hash, this_rowid);
        if (!foundSet.isEmpty()) {
            newGridForSet(foundSet);
        }
        return foundSet;
    }
}


