/*
 * JPty - A small PTY interface for Java.
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package pty4j;

import java.util.Objects;

public final class WinSize {
  // number of columns
  private final int myColumns;

  // number of rows
  private final int myRows;

  /**
   * @deprecated use {@link #getColumns()} instead
   */
  @SuppressWarnings("DeprecatedIsStillUsed")
  @Deprecated
  public short ws_col;

  /**
   * @deprecated use {@link #getRows()} instead
   */
  @SuppressWarnings("DeprecatedIsStillUsed")
  @Deprecated
  public short ws_row;

  /**
   * @deprecated unused field
   */
  @Deprecated
  public short ws_xpixel;

  /**
   * @deprecated unused field
   */
  @Deprecated
  public short ws_ypixel;

  /**
   * Creates a new, empty, {@link WinSize} instance
   */
  public WinSize() {
    this(0, 0);
  }

  /**
   * Creates a new {@link WinSize} instance for the given columns and rows.
   */
  public WinSize(int columns, int rows) {
    myColumns = columns;
    myRows = rows;
    ws_col = (short)columns;
    ws_row = (short)rows;
  }

  /**
   * @deprecated use {@link #WinSize(int, int)} instead
   */
  @SuppressWarnings("unused")
  @Deprecated
  public WinSize(int columns, int rows, int width, int height) {
    this(columns, rows);
  }

  public int getColumns() {
    return myColumns;
  }

  public int getRows() {
    return myRows;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    WinSize winSize = (WinSize) o;
    return myColumns == winSize.myColumns && myRows == winSize.myRows && ws_row == winSize.ws_row && ws_col == winSize.ws_col;
  }

  @Override
  public int hashCode() {
    return Objects.hash(myColumns, myRows, ws_row, ws_col);
  }

  @Override
  public String toString() {
    return "columns=" + myColumns  + ", rows=" + myRows + ", ws_col=" + ws_col + ", ws_row=" + ws_row;
  }
}
