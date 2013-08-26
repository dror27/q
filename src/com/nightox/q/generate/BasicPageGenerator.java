package com.nightox.q.generate;

import java.awt.Color;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import org.apache.pdfbox.exceptions.COSVisitorException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.edit.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.xobject.PDJpeg;
import org.apache.pdfbox.pdmodel.graphics.xobject.PDXObjectImage;

import com.google.zxing.WriterException;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.google.zxing.qrcode.encoder.ByteMatrix;
import com.google.zxing.qrcode.encoder.Encoder;
import com.google.zxing.qrcode.encoder.QRCode;

public class BasicPageGenerator {
	
	enum DotStyle
	{
		SQUARE,
		DOT,
		POLYGON1,
		POLYGON2
	}
	
	private String		baseUrl = "http://nightox.com/q/";
	private String		noteText = "#Nightox";
	private	int			codeRows = 3;
	private int			codeCols = 2;
	private float		spacingRatio = 0.20f;
	private float		marginX = 50;
	private float		marginY = 50;
	private float		fontSizeRatio = 0.07f;
	private float		noteSpacing = 0.8f;
	private DotStyle	dotStyle = DotStyle.SQUARE;
	private int			centerImageDotSize = 7;

	public static void main(String[] args) 
	{
		// extract args
		String					outputPath = args.length > 0 ? args[0] : "BasicPageGenerator.pdf";
		
		// generate
		BasicPageGenerator		generator = new BasicPageGenerator();
		try
		{
			OutputStream		outputStream = new FileOutputStream(outputPath);
			InputStream			centerImageStream = new FileInputStream( "Revolution-Fist-Small.jpg");
			
			generator.generatePage(outputStream, centerImageStream);
			outputStream.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public void generatePage(OutputStream outputStream, InputStream centerImageStream) throws IOException, COSVisitorException, WriterException
	{
		// create a document and add a page to it
		PDDocument	 		document = new PDDocument();
		PDPage page = new PDPage();
		document.addPage(page);

		// create a new font object selecting one of the PDF base fonts
		PDFont 			font2 = PDType1Font.HELVETICA_BOLD;
		PDFont 			font = new PDType1Font( "Arial" );

        // load image
        PDXObjectImage 		centerImage = null;
        if ( centerImageStream != null )
        	centerImage = new PDJpeg(document, centerImageStream);
        
		// start a new content stream which will "hold" the to be created content
		PDPageContentStream	contentStream = new PDPageContentStream(document, page);
        contentStream.setNonStrokingColor(Color.BLACK);
		
		// determine bounding box
        PDRectangle 		pageSize = page.findMediaBox();
		PDRectangle			boundingBox = new PDRectangle(pageSize.getWidth(), pageSize.getHeight());
		boundingBox.setLowerLeftX(marginX);
		boundingBox.setLowerLeftY(marginY);
		boundingBox.setUpperRightX(boundingBox.getUpperRightX() - marginX);
		boundingBox.setUpperRightY(boundingBox.getUpperRightY() - marginY);
		
		// determine size of code and spacing
		float				spacedCols = ((codeCols - 1) * (1 + spacingRatio) + 1);
		float				spacedRows = ((codeRows - 1) * (1 + spacingRatio) + 1);
		float				codeSizeX = boundingBox.getWidth() / spacedCols;
		float				codeSizeY = boundingBox.getHeight() / spacedRows;
		float				codeSize = Math.min(codeSizeX, codeSizeY);
		
		// adjust bounding box
		float				paddingX = (boundingBox.getWidth() - codeSize * spacedCols) / 2;
		float				paddingY = (boundingBox.getHeight() - codeSize * spacedRows) / 2;
		boundingBox.setLowerLeftX(boundingBox.getLowerLeftX() + paddingX);
		boundingBox.setLowerLeftY(boundingBox.getLowerLeftY() + paddingY);
		boundingBox.setUpperRightX(boundingBox.getUpperRightX() - paddingX);
		boundingBox.setUpperRightY(boundingBox.getUpperRightY() - paddingY);
		
		// setup font size
		float				fontSize = codeSize * fontSizeRatio;
		contentStream.setFont(font, fontSize);

		
		// walk the codes
		for ( int codeRow = 0 ; codeRow < codeRows ; codeRow++ )
			for ( int codeCol = 0 ; codeCol < codeCols ; codeCol++ )
			{
				// generate url
				String		id = UUID.randomUUID().toString().replace("-", "");
				String		url = baseUrl + id;
				
				// encode the url
				QRCode		code = Encoder.encode(url, ErrorCorrectionLevel.M);
				ByteMatrix	matrix = code.getMatrix();
				
				// establish base point & size
				float		baseX = boundingBox.getLowerLeftX() + codeSize * (codeCol * (1 + spacingRatio));
				float		baseY = boundingBox.getLowerLeftY() + codeSize * (codeRow * (1 + spacingRatio));
				float		size = codeSize / matrix.getWidth();

				// write the note
				contentStream.beginText();
				contentStream.moveTextPositionByAmount(baseX, baseY - fontSize * noteSpacing);
				contentStream.drawString(noteText);
				contentStream.endText();

				// write the dots
				for ( int row = 0 ; row < matrix.getHeight() ; row++ )
					for ( int col = 0 ; col < matrix.getWidth() ; col++ )
					{
						float		x = baseX + col * size;
						float		y = baseY + matrix.getHeight() * size - row * size;
						
						if ( matrix.get(col, row) != 0 )
						{
							if ( dotStyle == DotStyle.SQUARE )
								contentStream.fillRect(x, y, size, size);
							else if ( dotStyle == DotStyle.DOT )
							{
								float		radius = size / 2;
								float		centerX = x + radius;
								float		centerY = y + radius;
								
								DrawCirclePdfBox.circleFill(contentStream, centerX, centerY, radius);
							}
							else if ( dotStyle == DotStyle.POLYGON1 )
							{
								float[]		vx = new float[4];
								float[]		vy = new float[4];
								float		ratio = 0.1f;
								
								vx[0] = x;
								vy[0] = y;
								
								vx[1] = x;
								vy[1] = y + size;
								
								vx[2] = x + size;
								vy[2] = y + size;
								
								vx[3] = x + size;
								vy[3] = y;

								vx[1] += size * ratio;
								vx[2] -= size * ratio;
																
								contentStream.fillPolygon(vx, vy);
							}
							else if ( dotStyle == DotStyle.POLYGON2 )
							{
								float[]		vx = new float[4];
								float[]		vy = new float[4];
								float		ratio = 0.1f;
								
								vx[0] = x;
								vy[0] = y;
								
								vx[1] = x;
								vy[1] = y + size;
								
								vx[2] = x + size;
								vy[2] = y + size;
								
								vx[3] = x + size;
								vy[3] = y;

								if ( (row % 2) == 0 )
								{
									vx[1] += size * ratio;
									vx[2] -= size * ratio;
								}
								else
								{
									vx[0] += size * ratio;
									vx[3] -= size * ratio;
								}
																
								contentStream.fillPolygon(vx, vy);
							}
						}
					}
				
				// write the center image
				if ( centerImage != null )
				{
					int			ofs = (matrix.getWidth() - centerImageDotSize) / 2;
					float		imageX = baseX + ofs * size;
					float		imageY = baseY + (ofs + 1) * size;
					
					contentStream.drawXObject(centerImage, imageX, imageY, centerImageDotSize * size, centerImageDotSize * size);
				}
			}
		
		// make sure that the content stream is closed:
		contentStream.close();

		// save the results and ensure that the document is properly closed:
		//System.out.println("outputPath: " + outputPath);
		document.save(outputStream);
		document.close();
	}

	public int getCodeRows() {
		return codeRows;
	}

	public void setCodeRows(int codeRows) {
		this.codeRows = codeRows;
	}

	public int getCodeCols() {
		return codeCols;
	}

	public void setCodeCols(int codeCols) {
		this.codeCols = codeCols;
	}

	public String getNoteText() {
		return noteText;
	}

	public void setNoteText(String noteText) {
		this.noteText = noteText;
	}

}
