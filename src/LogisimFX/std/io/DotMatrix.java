/*
 * Logisim-evolution - digital logic design tool and simulator
 * Copyright by the Logisim-evolution developers
 *
 * https://github.com/logisim-evolution/
 *
 * This is free software released under GNU GPLv3 license
 */

package LogisimFX.std.io;

import LogisimFX.data.*;
import LogisimFX.std.LC;

public class DotMatrix extends DotMatrixBase {

	public DotMatrix() {
		super("DotMatrix", LC.createStringBinding("dotMatrixComponent"), 5, 7, new DotMatrixHdlGeneratorFactory());
	}

	public static final Attribute<BitWidth> ATTR_MATRIX_COLS =
			Attributes.forBitWidth("matrixcols", LC.createStringBinding("ioMatrixCols"), 1, Value.MAX_WIDTH);
	public static final Attribute<BitWidth> ATTR_MATRIX_ROWS =
			Attributes.forBitWidth("matrixrows", LC.createStringBinding("ioMatrixRows"), 1, Value.MAX_WIDTH);

	@Override
	public Attribute<BitWidth> getAttributeRows() {
		return ATTR_MATRIX_ROWS;
	}

	@Override
	public Attribute<BitWidth> getAttributeColumns() {
		return ATTR_MATRIX_COLS;
	}

	@Override
	public Attribute<AttributeOption> getAttributeShape() {
		return ATTR_DOT_SHAPE;
	}

	@Override
	public AttributeOption getDefaultShape() {
		return SHAPE_SQUARE;
	}

	@Override
	public Attribute<AttributeOption> getAttributeInputType() {
		return ATTR_INPUT_TYPE;
	}

	@Override
	public AttributeOption getAttributeItemColumn() {
		return INPUT_COLUMN;
	}

	@Override
	public AttributeOption getAttributeItemRow() {
		return INPUT_ROW;
	}

	@Override
	public AttributeOption getAttributeItemSelect() {
		return INPUT_SELECT;
	}

}
