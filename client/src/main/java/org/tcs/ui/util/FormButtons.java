package org.tcs.ui.util;

import javafx.scene.control.Button;
import javafx.scene.layout.HBox;

public class FormButtons extends HBox {
  public FormButtons(Runnable onSubmit, Runnable onCancel) {
    var submit = new Button("Submit");
    submit.setOnAction(_ -> onSubmit.run());
    var cancel = new Button("Cancel");
    cancel.setOnAction(_ -> onCancel.run());

    getChildren().addAll(submit, cancel);
  }
}
