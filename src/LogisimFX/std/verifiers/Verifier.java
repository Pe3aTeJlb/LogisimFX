/*
 * This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
 * License information is located in the Launch file
 */

package LogisimFX.std.verifiers;

import LogisimFX.IconsManager;
import LogisimFX.data.*;
import LogisimFX.instance.*;
import LogisimFX.newgui.MainFrame.EditorTabs.Graphics;
import LogisimFX.newgui.MainFrame.EditorTabs.LayoutEditor.layoutCanvas.LayoutCanvas;
import LogisimFX.std.LC;
import LogisimFX.std.wiring.Clock;
import LogisimFX.tools.key.BitWidthConfigurator;
import LogisimFX.util.GraphicsUtil;

import javafx.scene.image.ImageView;

public class Verifier extends Clock {

    private static final int DELAY = 8;

    private static final int IN0   = 0;
    private static final int OUT0  = 1;
    private static final int OUT1  = 2;

    //only for tty
    private static boolean restrictProp = false;

    private Attribute<AttributeOption> triggerAttribute;
    private Attribute<BitWidth> SEQUENCE_WIDTH =
            Attributes.forBitWidth("sequencewidth", LC.createStringBinding("stdSequenceWidthAttr"));

    private static final ImageView icon = IconsManager.getIcon("projapp.gif");

    private final Bounds bounds;

    public Verifier() {

        super("Verifier", LC.createStringBinding("verifier"));

        setAttributes(new Attribute[] {
                StdAttr.WIDTH, StdAttr.TRIGGER, Clock.ATTR_HIGH, Clock.ATTR_LOW, SEQUENCE_WIDTH
        }, new Object[] {
                BitWidth.create(1), StdAttr.TRIG_HIGH, 1, 1, BitWidth.create(4)
        });

        setKeyConfigurator(new BitWidthConfigurator(StdAttr.WIDTH));
        setOffsetBounds(bounds = Bounds.create(-30, -20, 30, 40));
        setIcon("projapp.gif");
        setInstanceLogger(Logger.class);
        setInstancePoker(VerifierPoker.class);

        Port[] ps = new Port[3];

        ps[IN0]   = new Port(-30, 0, Port.INPUT,  StdAttr.WIDTH);
        ps[OUT0]  = new Port(0,  -10, Port.OUTPUT,  SEQUENCE_WIDTH);
        ps[OUT1]  = new Port(  0,   10, Port.OUTPUT, 1);

        ps[IN0].setToolTip(LC.createStringBinding("verifierInputTip"));
        ps[OUT0].setToolTip(LC.createStringBinding("verifierOutput0Tip"));
        ps[OUT1].setToolTip(LC.createStringBinding("verifierOutput1Tip"));

        setPorts(ps);

    }

    public void setRestrictProp(boolean val){
        restrictProp = val;
    }

    public boolean isReadyOutput(InstanceState state){
        return state.getPortValue(OUT1) == Value.TRUE;
    }

    @Override
    protected void configureNewInstance(Instance instance) {

        Bounds bds = instance.getBounds();
        instance.setTextField(StdAttr.LABEL, StdAttr.LABEL_FONT,
                bds.getX() + bds.getWidth() / 2, bds.getY() - 3,
                GraphicsUtil.H_CENTER, GraphicsUtil.V_BASELINE);

    }

    @Override
    public Bounds getOffsetBounds(AttributeSet attrs) {
        return bounds;
    }

    @Override
    public void propagate(InstanceState state) {

        VerifierData data = null;

        if(state.getData() instanceof VerifierData)
            data = (VerifierData) state.getData();

        if (data == null) {

            int seqWidth = state.getAttributeValue(SEQUENCE_WIDTH).getWidth();

            data = new VerifierData(seqWidth, (int)Math.pow(2, seqWidth), state.getTickCount(), state.getAttributeSet());
            state.setData(data);

        }

        int oldVal = data.getClockVal().toIntValue();

        if((data.sending.equals(Value.TRUE) && !data.currentClock.equals(data.sending))) {

            if (oldVal < data.maxClock - 1) {

                int newVal;

                if(data.isClockZero){
                    data.isClockZero = false;
                    newVal = oldVal;
                }else {
                    newVal = oldVal + 1;
                }

                Value val = Value.createKnown(BitWidth.create(data.seqWidth), newVal);
                data.setClockVal(val);

            }

        }

        state.setPort(OUT0, data.getClockVal(), DELAY);

        //Object triggerType = state.getAttributeValue(triggerAttribute);
        //boolean triggered = data.updateClock(state.getPort(IN0), triggerType);

       // System.out.println("port is "+state.getPort(IN0));


        if (oldVal < data.maxClock - 1) {
            data.setValue(state.getPortValue(IN0));
            state.setPort(OUT1, Value.FALSE, DELAY);
        }else{
            state.setPort(OUT1, Value.TRUE, DELAY);
        }

        data.currentClock = data.sending;

    }

    @Override
    public void paintInstance(InstancePainter painter) {

        Graphics g = painter.getGraphics();
        Bounds bds = painter.getInstance().getBounds();

        VerifierData state = null;

        if(painter.getData() instanceof VerifierData)
            state = (VerifierData) painter.getData();

        // draw boundary, label
        painter.drawBounds();
        painter.drawLabel();

        // draw input and output ports
        painter.drawPort(IN0);
        painter.drawPort(OUT0);
        painter.drawPort(OUT1);

        // draw contents
        if (painter.getShowState()) {
            String str;
            if(state != null) {
                str = state.getValue().toHexString();
            }else{
                str = "x";
            }
            if (str.length() <= 4) {
                GraphicsUtil.drawText(g, str,
                        bds.getX() + 15, bds.getY() + 4,
                        GraphicsUtil.H_CENTER, GraphicsUtil.V_TOP);
            } else {
                int split = str.length() - 4;
                GraphicsUtil.drawText(g, str.substring(0, split),
                        bds.getX() + 15, bds.getY() + 3,
                        GraphicsUtil.H_CENTER, GraphicsUtil.V_TOP);
                GraphicsUtil.drawText(g, str.substring(split),
                        bds.getX() + 15, bds.getY() + 15,
                        GraphicsUtil.H_CENTER, GraphicsUtil.V_TOP);
            }
        }

        g.toDefault();

    }

    @Override
    public ImageView getIcon(){
        return icon;
    }

    private static class VerifierData extends ClockState {

        private Value currentClock = Value.FALSE;
        //private Value lastClock = Value.FALSE;
        private Value clockVal;
        private int seqWidth, maxClock;
        private boolean isClockZero = true;

        private Value[] vals;

        public VerifierData(int seqWidth, int maxClock, long curTick, AttributeSet attrs) {
            super(curTick, attrs);
            this.seqWidth = seqWidth;
            this.maxClock = maxClock;
            clockVal = Value.createKnown(BitWidth.create(seqWidth),0);
            vals = new Value[maxClock];
        }

        @Override
        public VerifierData clone() {
            return (VerifierData) super.clone();
        }

        public Value getClockVal() {
            return clockVal;
        }

        public void setClockVal(Value val){
            if(!restrictProp)
                clockVal = val;

        }

        public void setValue(Value value) {
            if(!restrictProp)
                vals[clockVal.toIntValue()] = value;
        }

        public Value getValue() {
            return (vals == null || vals[clockVal.toIntValue()] == null) ? Value.NIL : vals[clockVal.toIntValue()];
        }

        public void clearValues(){
            vals = new Value[maxClock];
        }

    }

    public static class Logger extends InstanceLogger {

        @Override
        public String getLogName(InstanceState state, Object option) {
            return state.getAttributeValue(StdAttr.LABEL);
        }

        @Override
        public Value getLogValue(InstanceState state, Object option) {
            VerifierData data = (VerifierData) state.getData();
            return data == null ? Value.NIL : data.getValue();
        }

    }

    public static class VerifierPoker extends InstancePoker{

        @Override
        public void mousePressed(InstanceState state, LayoutCanvas.CME e) {
        }

        @Override
        public void mouseReleased(InstanceState state, LayoutCanvas.CME e) {
        }

    }

}
