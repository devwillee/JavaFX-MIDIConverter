package io.github.xeyez;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.stage.FileChooser;

import javax.sound.midi.*;
import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

/*
 *	MidiConverter.java
 *
 *	This file is part of jsresources.org
 */

/*
 * Copyright (c) 1999 by Matthias Pfisterer
 * Copyright (c) 2003 by Florian Bomers
 * Copyright (c) 2015 by Joohyuk Lee
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * - Redistributions of source code must retain the above copyright notice,
 *   this list of conditions and the following disclaimer.
 * - Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 * COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */

/*
|<---            this code is formatted to fit into 80 columns             --->|
*/


public class Controller implements Initializable {
    @FXML
    private Button btn;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        btn.setOnAction(event -> handleBtnAction(event));
    }

    public void handleBtnAction(ActionEvent actionEvent) {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("MIDI Type1 Files", "*.midi", "*.mid"));
            File selectedFile = fileChooser.showOpenDialog(btn.getScene().getWindow());

            if(selectedFile == null)
                return;

            MidiFileFormat mff = MidiSystem.getMidiFileFormat(selectedFile);
            if(mff.getType() == 0)
                throw new Exception("The input file you specified is already a type 0 MIDI file.");

            Sequence sequence = MidiSystem.getSequence(selectedFile);
            Track[]	aTracks = sequence.getTracks();
            if (aTracks.length == 0)
                throw new Exception("The input file you specified does not contain any tracks! exit.");

            Track firstTrack = aTracks[0];
            int nTrack;

            for (nTrack = 1; nTrack < aTracks.length; nTrack++)
            {
                Track track = aTracks[nTrack];

                // add all events of this track to the first track
                for (int i = 0; i < track.size(); i++)
                {
                    firstTrack.add(track.get(i));
                }

                // delete this track from the sequence
                sequence.deleteTrack(track);
            }

            int index_extension = selectedFile.getAbsolutePath().lastIndexOf('.');
            String newFilePath = selectedFile.getAbsolutePath().substring(0, index_extension) + "_converted.mid";

            int written = MidiSystem.write(sequence, 0, new File(newFilePath));
            new Alert(Alert.AlertType.NONE, "Wrote "+newFilePath+" successfully ("+written+" bytes).", ButtonType.OK).show();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage(), ButtonType.OK);
            alert.show();
        }
    }
}
