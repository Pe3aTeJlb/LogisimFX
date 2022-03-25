/*
 * This file is part of LogisimFX by Pplos Studio.
 *
 * https://github.com/Pe3aTeJlb/LogisimFX
 * https://sites.google.com/view/pplosstudio/%D0%B3%D0%BB%D0%B0%D0%B2%D0%BD%D0%B0%D1%8F
 *
 * LogisimFX is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by the
 * Free Software Foundation, either version 2 of the License, or (at your
 * option) any later version.
 *
 * LogisimFX is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with LogisimFX. If not, see <http://www.gnu.org/licenses/>.
 *
 * Original code by Carl Burch (http://www.cburch.com), 2011.
 */

import LogisimFX.Main;

public class Launcher {
    public static void main(String[] args) {
//-Dprism.order=sw -Dprism.dirtyopts=false -Djavafx.pulseLogger=true
        System.setProperty("prism.vsync", "true");
        System.setProperty("quantum.multithreading", "true");
        System.setProperty("javafx.animation.fullspeed", "false");
        System.setProperty("javafx.animation.pulse", "60");
        System.setProperty("javafx.animation.framerate", "60");
        System.setProperty("sun.java2d.opengl", "False");
        System.setProperty("sun.java2d.d3d", "False");

        Main.main(args);

    }
}