package com.nightox.q.generate;

import java.awt.geom.PathIterator;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDStream;
import org.apache.pdfbox.pdmodel.edit.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.color.PDColorSpace;
import org.apache.pdfbox.pdmodel.graphics.color.PDDeviceCMYK;
import org.apache.pdfbox.pdmodel.graphics.color.PDICCBased;

public class DrawCirclePdfBox {
	private static NumberFormat formatDecimal = NumberFormat
			.getNumberInstance(Locale.US);
	private static final int SPACE = 32;

	public static void curveTo(PDPageContentStream contentStream,
			final float x1, final float y1, final float x2, final float y2,
			final float x3, final float y3) throws IOException {
		contentStream.appendRawCommands(formatDecimal.format(x1));
		contentStream.appendRawCommands(SPACE);
		contentStream.appendRawCommands(formatDecimal.format(y1));
		contentStream.appendRawCommands(SPACE);
		contentStream.appendRawCommands(formatDecimal.format(x2));
		contentStream.appendRawCommands(SPACE);
		contentStream.appendRawCommands(formatDecimal.format(y2));
		contentStream.appendRawCommands(SPACE);
		contentStream.appendRawCommands(formatDecimal.format(x3));
		contentStream.appendRawCommands(SPACE);
		contentStream.appendRawCommands(formatDecimal.format(y3));
		contentStream.appendRawCommands(SPACE);
		contentStream.appendRawCommands("c\n");

	}

	public static void circle(PDPageContentStream contentStream, final float x,
			final float y, final float r) throws IOException {
		float b = 0.5523f;
		contentStream.moveTo(x + r, y);
		curveTo(contentStream, x + r, y + r * b, x + r * b, y + r, x, y + r);
		curveTo(contentStream, x - r * b, y + r, x - r, y + r * b, x - r, y);
		curveTo(contentStream, x - r, y - r * b, x - r * b, y - r, x, y - r);
		curveTo(contentStream, x + r * b, y - r, x + r, y - r * b, x + r, y);
	}

	public static void circleFill(PDPageContentStream contentStream,
			final float x, final float y, final float r) throws IOException {
		float b = 0.5523f;
		contentStream.moveTo(x + r, y);
		curveTo(contentStream, x + r, y + r * b, x + r * b, y + r, x, y + r);
		curveTo(contentStream, x - r * b, y + r, x - r, y + r * b, x - r, y);
		curveTo(contentStream, x - r, y - r * b, x - r * b, y - r, x, y - r);
		curveTo(contentStream, x + r * b, y - r, x + r, y - r * b, x + r, y);
		contentStream.fill(PathIterator.WIND_NON_ZERO);
	}

	public static void main(String[] args) throws Exception {

		File iccProfile = new File(
				"e:/Projetos/ocp_fontes/cre/Com.Hp.Sra.Ocp.Cre/Resources/substrates/pe34483/pe34483.icc");

		PDDocument document = new PDDocument();

		PDStream pdStream = new PDStream(document, new FileInputStream(
				iccProfile));

		COSArray array = new COSArray();
		array.add(COSName.ICCBASED);
		array.add(pdStream.getStream());
		PDICCBased pdiccBased = new PDICCBased(array);

		List<PDColorSpace> altList = new LinkedList<PDColorSpace>();
		altList.add(PDDeviceCMYK.INSTANCE);
		pdiccBased.setAlternateColorSpaces(altList);

		PDPage page = new PDPage();
		document.addPage(page);
		PDPageContentStream contentStream = new PDPageContentStream(document,
				page);

		contentStream.setNonStrokingColor(0, 13, 43, 16);
		contentStream.setStrokingColor(0, 13, 43, 16);
		circleFill(contentStream, 100, 100, 50);
		contentStream.closeAndStroke();
		

		contentStream.setNonStrokingColorSpace(pdiccBased);
		contentStream.setNonStrokingColor(new float[] { 0, 13, 43, 16 });

		contentStream.setStrokingColorSpace(pdiccBased);
		contentStream.setStrokingColor(new float[] { 0, 13, 43, 16 });

		circleFill(contentStream, 200, 200, 50);

		contentStream.closeAndStroke();
		contentStream.close();

		page.updateLastModified();

		document.save("c:\\temp\\circles.pdf");
		document.close();
	}
}
