package org.tcs.ui;

import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.HBox;

import java.util.function.BiFunction;

public class Util {
  public static HBox inline(Node... children) {
    var res = new HBox(4, children);
    res.setAlignment(Pos.CENTER_LEFT);
    return res;
  }

  public static <A, B, R> ObservableValue<R> bindNotNull(
    ObservableValue<A> propA,
    ObservableValue<B> propB,
    BiFunction<A, B, R> compute) {

    return propA.flatMap(a -> propB.map(b -> compute.apply(a, b)));
  }
}
