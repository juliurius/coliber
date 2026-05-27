package org.tcs.ui;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.HBox;

public class Util {
  public static HBox inline(Node... children) {
    var res = new HBox(children);
    res.setAlignment(Pos.CENTER_LEFT);
    return res;
  }
}
