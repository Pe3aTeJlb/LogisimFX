package LogisimFX.fpga.data;

import LogisimFX.newgui.IOMapper.IOMapperController;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class BoardRectangle extends Rectangle {

	private Color DEFAULT_COLOR = Color.color(0.7, 0, 1, 0.5);
	private Color DEFAULT_FOCUSED_COLOR = Color.color(0.8, 0.35, 1, 0.5);
	private Color MAPPED_COLOR = Color.color(1, 0, 0, 0.5);
	private Color MAPPED_FOCUSED_COLOR = Color.color(1, 0.3, 0.3, 0.5);
	private Color CURRENT_COLOR, CURRENT_FOCUSED_COLOR;

	private FpgaIoInformationContainer container;

	public BoardRectangle(FpgaIoInformationContainer container, double x, double y, double w, double h){
		super(x, y, w, h);
		this.container = container;
		setEvents();
	}

	public BoardRectangle(double x, double y, double w, double h){
		super(x, y, w, h);
		setEvents();
	}

	public void updateFpgaIoInformationContainer(FpgaIoInformationContainer container){
		this.container = container;
	}

	private void setEvents(){

		CURRENT_COLOR = DEFAULT_COLOR;
		CURRENT_FOCUSED_COLOR = DEFAULT_FOCUSED_COLOR;

		this.setFill(DEFAULT_COLOR);

		this.setOnMouseEntered(event -> this.setFill(CURRENT_FOCUSED_COLOR));

		this.setOnMouseExited(event -> this.setFill(CURRENT_COLOR));

	}

	public void checkPin(IOMapperController.MapInfo info){

		if (container.isPinMapped(info.getPin())){
			CURRENT_COLOR = MAPPED_COLOR;
			CURRENT_FOCUSED_COLOR = MAPPED_FOCUSED_COLOR;
		} else {
			CURRENT_COLOR = DEFAULT_COLOR;
			CURRENT_FOCUSED_COLOR = DEFAULT_FOCUSED_COLOR;
		}

		this.setFill(CURRENT_COLOR);

	}

	public boolean onMouseClicked(){
		if (container != null) {
			return container.tryMap();
		} else return false;
	}

}
