/*
 * Logisim-evolution - digital logic design tool and simulator
 * Copyright by the Logisim-evolution developers
 *
 * https://github.com/logisim-evolution/
 *
 * This is free software released under GNU GPLv3 license
 */

package LogisimFX.fpga.file;

import LogisimFX.fpga.data.BoardInformation;
import LogisimFX.fpga.data.FpgaClass;
import LogisimFX.fpga.data.FpgaIoInformationContainer;
import LogisimFX.newgui.DialogManager;
import LogisimFX.util.XmlUtil;
import javafx.scene.image.Image;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Base64;

public class BoardReaderClass {

	public static final String BOARD_INFORMATION_SECTION_STRING = "BoardInformation";
	public static final String CLOCK_INFORMATION_SECTION_STRING = "ClockInformation";
	public static final String INPUT_SET_STRING = "InputPinSet";
	public static final String OUTPUT_SET_STRING = "OutputPinSet";
	public static final String IO_SET_STRING = "BiDirPinSet";
	public static final String RECT_SET_STRING = "Rect_x_y_w_h";
	public static final String LED_ARRAY_INFO_STRING = "LedArrayInfo";
	public static final String MAP_ROTATION = "rotation";
	public static final String[] CLOCK_SECTION_STRINGS = {
			"Frequency", "FPGApin", "PullBehavior", "IOStandard"
	};
	public static final String FPGA_INFORMATION_SECTION_STRING = "FPGAInformation";
	public static final String[] FPGA_SECTION_STRINGS = {
			"Vendor",
			"Part",
			"Family",
			"Package",
			"Speedgrade",
			"USBTMC",
			"JTAGPos",
			"FlashName",
			"FlashPos",
			"F4PGATarget"
	};
	public static final String[] VENDORS = {"Altera", "Xilinx", "Vivado"};
	public static final String UNUSED_PINS_STRING = "UnusedPins";
	public static final String COMPONENTS_SECTION_STRING = "IOComponents";
	public static final String LOCATION_X_STRING = "LocationX";
	public static final String LOCATION_Y_STRING = "LocationY";
	public static final String WIDTH_STRING = "Width";
	public static final String HEIGHT_STRING = "Height";
	public static final String PIN_LOCATION_STRING = "FPGAPinName";
	public static final String IMAGE_INFORMATION_STRING = "BoardPicture";
	public static final String MULTI_PIN_INFORMATION_STRING = "NrOfPins";
	public static final String MULTI_PIN_PREFIX_STRING = "FPGAPin_";
	public static final String LABEL_STRING = "Label";

	private final InputStream inputStream;
	private DocumentBuilderFactory factory;
	private DocumentBuilder parser;
	private Document BoardDoc;

	public BoardReaderClass(InputStream stream) {
		inputStream = stream;
	}

	public BoardInformation getBoardInformation() {
		try {
			// Create instance of DocumentBuilderFactory
			factory = XmlUtil.getHardenedBuilderFactory();
			// Get the DocumentBuilder
			parser = factory.newDocumentBuilder();
			// Create blank DOM Document
			BoardDoc = parser.parse(inputStream);

			NodeList ImageList = BoardDoc.getElementsByTagName(BoardReaderClass.IMAGE_INFORMATION_STRING);
			if (ImageList.getLength() != 1) return null;
			Node ThisImage = ImageList.item(0);
			NodeList ImageParameters = ThisImage.getChildNodes();

			int PictureWidth = 0;
			int PictureHeight = 0;
			String base64String = null;
			for (int i = 0; i < ImageParameters.getLength(); i++) {
				if (ImageParameters.item(i).getNodeName().equals("PictureDimension")) {
					NamedNodeMap SizeAttrs = ImageParameters.item(i).getAttributes();
					for (int j = 0; j < SizeAttrs.getLength(); j++) {
						if (SizeAttrs.item(j).getNodeName().equals("Width"))
							PictureWidth = Integer.parseInt(SizeAttrs.item(j).getNodeValue());
						if (SizeAttrs.item(j).getNodeName().equals("Height"))
							PictureHeight = Integer.parseInt(SizeAttrs.item(j).getNodeValue());
					}
				}
				if (ImageParameters.item(i).getNodeName().equals("Base64")) {
					NamedNodeMap dataAttr = ImageParameters.item(i).getAttributes();
					for (int j = 0; j < dataAttr.getLength(); j++)
						if (dataAttr.item(j).getNodeName().equals("data"))
							base64String = dataAttr.item(j).getNodeValue();
				}
			}

			if (base64String == null) {
				DialogManager.createErrorDialog("Error", "The selected XML file does not contain the picture data");
				return null;
			}

			BoardInformation result = new BoardInformation();
			result.setBoardName(BoardDoc.getDocumentElement().getNodeName());
			Image Picture = new Image(new ByteArrayInputStream(Base64.getDecoder().decode(base64String)));
			result.setImage(Picture);


			FpgaClass FPGA = getFpgaInfo();
			if (FPGA == null) return null;
			result.fpga = FPGA;
			NodeList CompList = BoardDoc.getElementsByTagName("PinsInformation"); // for backward
			// compatibility
			processComponentList(CompList, result);
			CompList = BoardDoc.getElementsByTagName("ButtonsInformation"); // for
			// backward
			// compatibility
			processComponentList(CompList, result);
			CompList = BoardDoc.getElementsByTagName("LEDsInformation"); // for
			// backward
			// compatibility
			processComponentList(CompList, result);
			CompList = BoardDoc.getElementsByTagName(BoardReaderClass.COMPONENTS_SECTION_STRING); // new
			// format
			processComponentList(CompList, result);
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			//logger.error(
			//		"Exceptions not handled yet in GetBoardInformation(), but got an exception: {}",
			//		e.getMessage());
			/* TODO: handle exceptions */
			return null;
		}
	}

	private FpgaClass getFpgaInfo() {
		var fpgaList = BoardDoc.getElementsByTagName(BoardReaderClass.BOARD_INFORMATION_SECTION_STRING);
		var frequency = -1L;
		String clockPin = null;
		String clockPull = null;
		String clockStand = null;
		String unusedPull = null;
		String vendor = null;
		String part = null;
		String family = null;
		String Package = null;
		String speed = null;
		String usbTmc = null;
		String jtagPos = null;
		String flashName = null;
		String flashPos = null;
		String f4pgatarget = null;
		if (fpgaList.getLength() != 1) return null;
		final var thisFpga = fpgaList.item(0);
		final var fpgaParams = thisFpga.getChildNodes();
		for (int i = 0; i < fpgaParams.getLength(); i++) {
			if (fpgaParams
					.item(i)
					.getNodeName()
					.equals(BoardReaderClass.CLOCK_INFORMATION_SECTION_STRING)) {
				final var clockAttrs = fpgaParams.item(i).getAttributes();
				for (int j = 0; j < clockAttrs.getLength(); j++) {
					if (clockAttrs.item(j).getNodeName().equals(BoardReaderClass.CLOCK_SECTION_STRINGS[0]))
						frequency = Long.parseLong(clockAttrs.item(j).getNodeValue());
					if (clockAttrs.item(j).getNodeName().equals(BoardReaderClass.CLOCK_SECTION_STRINGS[1]))
						clockPin = clockAttrs.item(j).getNodeValue();
					if (clockAttrs.item(j).getNodeName().equals(BoardReaderClass.CLOCK_SECTION_STRINGS[2]))
						clockPull = clockAttrs.item(j).getNodeValue();
					if (clockAttrs.item(j).getNodeName().equals(BoardReaderClass.CLOCK_SECTION_STRINGS[3]))
						clockStand = clockAttrs.item(j).getNodeValue();
				}
			}
			if (fpgaParams.item(i).getNodeName().equals(BoardReaderClass.UNUSED_PINS_STRING)) {
				final var unusedAttrs = fpgaParams.item(i).getAttributes();
				for (int j = 0; j < unusedAttrs.getLength(); j++)
					if (unusedAttrs.item(j).getNodeName().equals("PullBehavior"))
						unusedPull = unusedAttrs.item(j).getNodeValue();
			}
			if (fpgaParams
					.item(i)
					.getNodeName()
					.equals(BoardReaderClass.FPGA_INFORMATION_SECTION_STRING)) {
				final var fpgaAttrs = fpgaParams.item(i).getAttributes();
				for (int j = 0; j < fpgaAttrs.getLength(); j++) {
					if (fpgaAttrs.item(j).getNodeName().equals(BoardReaderClass.FPGA_SECTION_STRINGS[0]))
						vendor = fpgaAttrs.item(j).getNodeValue();
					if (fpgaAttrs.item(j).getNodeName().equals(BoardReaderClass.FPGA_SECTION_STRINGS[1]))
						part = fpgaAttrs.item(j).getNodeValue();
					if (fpgaAttrs.item(j).getNodeName().equals(BoardReaderClass.FPGA_SECTION_STRINGS[2]))
						family = fpgaAttrs.item(j).getNodeValue();
					if (fpgaAttrs.item(j).getNodeName().equals(BoardReaderClass.FPGA_SECTION_STRINGS[3]))
						Package = fpgaAttrs.item(j).getNodeValue();
					if (fpgaAttrs.item(j).getNodeName().equals(BoardReaderClass.FPGA_SECTION_STRINGS[4]))
						speed = fpgaAttrs.item(j).getNodeValue();
					if (fpgaAttrs.item(j).getNodeName().equals(BoardReaderClass.FPGA_SECTION_STRINGS[5]))
						usbTmc = fpgaAttrs.item(j).getNodeValue();
					if (fpgaAttrs.item(j).getNodeName().equals(BoardReaderClass.FPGA_SECTION_STRINGS[6]))
						jtagPos = fpgaAttrs.item(j).getNodeValue();
					if (fpgaAttrs.item(j).getNodeName().equals(BoardReaderClass.FPGA_SECTION_STRINGS[7]))
						flashName = fpgaAttrs.item(j).getNodeValue();
					if (fpgaAttrs.item(j).getNodeName().equals(BoardReaderClass.FPGA_SECTION_STRINGS[8]))
						flashPos = fpgaAttrs.item(j).getNodeValue();
					if (fpgaAttrs.item(j).getNodeName().equals(BoardReaderClass.FPGA_SECTION_STRINGS[9]))
						f4pgatarget = fpgaAttrs.item(j).getNodeValue();
				}
			}
		}
		if ((frequency < 0)
				|| (clockPin == null)
				|| (clockPull == null)
				|| (clockStand == null)
				|| (unusedPull == null)
				|| (vendor == null)
				|| (part == null)
				|| (family == null)
				|| (Package == null)
				|| (speed == null)) {
			// FIXME: hardcoded string
			DialogManager.createErrorDialog("Error", "The selected xml file does not contain the required FPGA parameters");
			return null;
		}
		if (usbTmc == null) usbTmc = Boolean.toString(false);
		if (jtagPos == null) jtagPos = "1";
		if (flashPos == null) flashPos = "2";
		FpgaClass result = new FpgaClass();
		result.set(
				frequency,
				clockPin,
				clockPull,
				clockStand,
				family,
				part,
				Package,
				speed,
				vendor,
				unusedPull,
				usbTmc.equals(Boolean.toString(true)),
				jtagPos,
				flashName,
				flashPos,
				f4pgatarget);
		return result;
	}

	private void processComponentList(NodeList compList, BoardInformation board) {
		Node tempNode = null;
		if (compList.getLength() == 1) {
			tempNode = compList.item(0);
			compList = tempNode.getChildNodes();
			for (var i = 0; i < compList.getLength(); i++) {
				final var newComp = new FpgaIoInformationContainer(compList.item(i));
				if (newComp.isKnownComponent()) {
					board.addComponent(newComp);
				}
			}
		}
	}
}
