package com.user.salestracking;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class HeaderFooterPageEvent extends PdfPageEventHelper {
    private static Context context;

    public HeaderFooterPageEvent(Context context) {
        this.context = context;
    }

    public void onStartPage(PdfWriter writer,Document document) {
        Image image = null;
        try {
            InputStream ims = context.getAssets().open("zakatpedia.png");
            Bitmap bmp = BitmapFactory.decodeStream(ims);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
            image = Image.getInstance(stream.toByteArray());
            image.scaleToFit(800,70);
            image.setAlignment(Element.ALIGN_RIGHT);
            document.add(image);
        } catch (com.itextpdf.text.DocumentException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
//    public void onEndPage(PdfWriter writer,Document document) {
//        Rectangle rect = writer.getBoxSize("art");
//        ColumnText.showTextAligned(writer.getDirectContent(),Element.ALIGN_CENTER, new Phrase("KANTOR PUSAT"), rect.getLeft(), rect.getBottom(), 0);
//        ColumnText.showTextAligned(writer.getDirectContent(),Element.ALIGN_CENTER, new Phrase("IZI(Inisiatif Zakat Indonesia)"), rect.getLeft(), rect.getBottom(), 0);
//        ColumnText.showTextAligned(writer.getDirectContent(),Element.ALIGN_CENTER, new Phrase("Jl. Raya Condet No 54 D-E Batu Ampar"), rect.getLeft(), rect.getBottom(), 0);
//        ColumnText.showTextAligned(writer.getDirectContent(),Element.ALIGN_CENTER, new Phrase("Jakarta Timur 13520-Indonesia"), rect.getLeft(), rect.getBottom(), 0);
//        ColumnText.showTextAligned(writer.getDirectContent(),Element.ALIGN_CENTER, new Phrase("Telp:(021)87787325 Fax.(021)"), rect.getLeft(), rect.getBottom(), 0);
//        ColumnText.showTextAligned(writer.getDirectContent(),Element.ALIGN_CENTER, new Phrase("Bottom Left"), rect.getLeft(), rect.getBottom(), 0);
//    }
}