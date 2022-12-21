package com.example.media;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.FileChooser;
import javafx.util.Duration;
import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.CyclicBarrier;


public class MediaController implements Initializable {

    public static MediaPlayer mediaPlayer;
    public static MediaPlayer mediaPlayer2;
    public static MediaPlayer mediaPlayer3;
    public static int countSeek = 0;
    public static int countPause = 1;
    public static int countFaster = 0;
    public static int countSlower = 0;
    private String filePath;
    public static CyclicBarrier berrier;

    @FXML
    private MediaView mediaView;

    @FXML
    private MediaView mediaView2;

    @FXML
    private MediaView mediaView3;

    @FXML
    public Slider slider;

    @FXML
    public Slider seekSlider;

    @FXML
    protected void onOpenButtonClick() {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("Select a file (*.mp4)", "*.mp4");
        fileChooser.getExtensionFilters().add(filter);
        File file;
        while(filePath == null) {
            file = fileChooser.showOpenDialog(null);
            filePath = file.toURI().toString();
        }
        if (filePath != null) {
            Media media = new Media(filePath);
            mediaPlayer = new MediaPlayer(media);
            mediaView.setMediaPlayer(mediaPlayer);
            DoubleProperty widht = mediaView.fitWidthProperty();
            DoubleProperty hight = mediaView.fitHeightProperty();

            widht.bind(Bindings.selectDouble(mediaView.sceneProperty(), "width").divide(2.3));
            hight.bind(Bindings.selectDouble(mediaView.sceneProperty(), "hight").divide(2.3));
        }
        filePath = null;
        while(filePath == null) {
            file = fileChooser.showOpenDialog(null);
            filePath = file.toURI().toString();
        }
        if (filePath != null) {
            Media media2 = new Media(filePath);
            mediaPlayer2 = new MediaPlayer(media2);
            mediaView2.setMediaPlayer(mediaPlayer2);
            DoubleProperty widht = mediaView2.fitWidthProperty();
            DoubleProperty hight = mediaView2.fitHeightProperty();

            widht.bind(Bindings.selectDouble(mediaView2.sceneProperty(), "width").divide(2.3));
            hight.bind(Bindings.selectDouble(mediaView2.sceneProperty(), "hight").divide(2.3));
        }
        filePath = null;
        while(filePath == null) {
            file = fileChooser.showOpenDialog(null);
            filePath = file.toURI().toString();
        }

        if (filePath != null) {
            Media media3 = new Media(filePath);
            mediaPlayer3 = new MediaPlayer(media3);
            mediaView3.setMediaPlayer(mediaPlayer3);
            DoubleProperty widht = mediaView3.fitWidthProperty();
            DoubleProperty hight = mediaView3.fitHeightProperty();

            widht.bind(Bindings.selectDouble(mediaView3.sceneProperty(), "width").divide(2.3));
            hight.bind(Bindings.selectDouble(mediaView3.sceneProperty(), "hight").divide(2.3));
        }
        slider.setValue(MediaController.mediaPlayer.getVolume() * 100);
        slider.valueProperty().addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable observable) {
                mediaPlayer.setVolume(slider.getValue()/100);
                mediaPlayer2.setVolume(slider.getValue()/100);
                mediaPlayer3.setVolume(slider.getValue()/100);
            }
        });
        VThread vThread1 = new VThread(seekSlider);
        VThread2 vThread2 = new VThread2(seekSlider);
        VThread3 vThread3 = new VThread3(seekSlider);
        vThread1.start();
        vThread2.start();
        vThread3.start();
        seekSlider.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                countSeek = 1;
            }
        });
    }
    @FXML
    private void pauseVideo(ActionEvent event) {
        countPause = 1;
    }

    @FXML
    private void playVideo(ActionEvent event) {
        countPause = 0;
    }

    @FXML
    private void fasterVideo(ActionEvent event) {
        countFaster = 1;
    }

    @FXML
    private void slowerVideo(ActionEvent event) {
        countSlower = 1;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}
class VThread extends Thread implements Runnable {
    private Slider slider;
    private int countPauseTab = 1;
    private int countPlayTab = 0;
     public VThread(Slider slider) {
         this.slider = slider;
     }
      public void run() {
          MediaController.berrier = new CyclicBarrier(3);
                  while(true) {
                      if(MediaController.countFaster == 1) {
                          try {
                              MediaController.mediaPlayer.pause();
                              MediaController.mediaPlayer.seek(MediaController.mediaPlayer.getCurrentTime().add(Duration.seconds(30)));
                              MediaController.countFaster = 0;
                              MediaController.berrier.await();
                              MediaController.mediaPlayer.play();
                          }catch(Exception e) {};
                      }
                      if(MediaController.countSlower == 1) {
                          try {
                              MediaController.mediaPlayer.pause();
                              MediaController.mediaPlayer.seek(MediaController.mediaPlayer.getCurrentTime().subtract(Duration.seconds(10)));
                              MediaController.countSlower = 0;
                              MediaController.berrier.await();
                              MediaController.mediaPlayer.play();
                          }catch(Exception e) {};
                      }
                      if(MediaController.countPause == 1 && countPauseTab == 0) {
                          try {
                              countPauseTab = 1;
                              countPlayTab = 0;
                              MediaController.berrier.await();
                              MediaController.mediaPlayer.pause();
                          }catch(Exception e) {};
                      }
                      if(MediaController.countPause == 0 && countPlayTab == 0) {
                          try {
                              countPlayTab = 1;
                              countPauseTab = 0;
                              MediaController.berrier.await();
                              MediaController.mediaPlayer.play();
                          }catch(Exception e) {};
                      }
                      if(MediaController.countSeek == 1) {
                          try{
                              MediaController.mediaPlayer.pause();
                              MediaController.mediaPlayer.seek(Duration.seconds(slider.getValue() / 100 * MediaController.mediaPlayer.getTotalDuration().toSeconds()));
                              MediaController.countSeek = 0;
                              MediaController.berrier.await();
                              MediaController.mediaPlayer.play();
                          }catch(Exception e) {};

                      }
                  }
      }
}
class VThread2 extends Thread implements Runnable {
    private Slider slider;
    private int countPauseTab = 1;
    private int countPlayTab = 0;
     public VThread2(Slider slider) {
         this.slider = slider;
     }
      public void run() {
          while(true) {
              if(MediaController.countFaster == 1) {
                  try {
                      MediaController.mediaPlayer2.pause();
                      MediaController.mediaPlayer2.seek(MediaController.mediaPlayer2.getCurrentTime().add(Duration.seconds(30)));
                      MediaController.countFaster = 0;
                      MediaController.berrier.await();
                      MediaController.mediaPlayer2.play();
                  }catch(Exception e) {};
              }
              if(MediaController.countSlower == 1) {
                  try {
                      MediaController.mediaPlayer2.pause();
                      MediaController.mediaPlayer2.seek(MediaController.mediaPlayer2.getCurrentTime().subtract(Duration.seconds(10)));
                      MediaController.countSlower = 0;
                      MediaController.berrier.await();
                      MediaController.mediaPlayer2.play();
                  }catch(Exception e) {};
              }
              if(MediaController.countPause == 1 && countPauseTab == 0) {
                  try {
                      countPauseTab = 1;
                      countPlayTab = 0;
                      MediaController.berrier.await();
                      MediaController.mediaPlayer2.pause();
                  }catch(Exception e) {};
              }
              if(MediaController.countPause == 0 && countPlayTab == 0) {
                  try {
                      countPlayTab = 1;
                      countPauseTab = 0;
                      MediaController.berrier.await();
                      MediaController.mediaPlayer2.play();
                  }catch(Exception e) {};
              }
              if(MediaController.countSeek == 1) {
                  try {
                      MediaController.mediaPlayer2.pause();
                      MediaController.mediaPlayer2.seek(Duration.seconds(slider.getValue() / 100 * MediaController.mediaPlayer2.getTotalDuration().toSeconds()));
                      MediaController.countSeek = 0;
                      MediaController.berrier.await();
                      MediaController.mediaPlayer2.play();
                  }catch(Exception e) {};

              }
          }
      }
}
class VThread3 extends Thread implements Runnable {
    private Slider slider;
    private int countPauseTab = 1;
    private int countPlayTab = 0;
     public VThread3(Slider slider) {
         this.slider = slider;
     }
      public void run() {
          while(true) {
              if(MediaController.countFaster == 1) {
                  try {
                      MediaController.mediaPlayer3.pause();
                      MediaController.mediaPlayer3.seek(MediaController.mediaPlayer3.getCurrentTime().add(Duration.seconds(30)));
                      MediaController.countFaster = 0;
                      MediaController.berrier.await();
                      MediaController.mediaPlayer3.play();
                  }catch(Exception e) {};

              }
              if(MediaController.countSlower == 1) {
                  try {
                      MediaController.mediaPlayer3.pause();
                      MediaController.mediaPlayer3.seek(MediaController.mediaPlayer3.getCurrentTime().subtract(Duration.seconds(10)));
                      MediaController.countSlower = 0;
                      MediaController.berrier.await();
                      MediaController.mediaPlayer3.play();
                  }catch(Exception e) {};

              }
              if(MediaController.countPause == 1 && countPauseTab == 0) {
                  try {
                      countPauseTab = 1;
                      countPlayTab = 0;
                      MediaController.berrier.await();
                      MediaController.mediaPlayer3.pause();
                  }catch(Exception e) {};

              }
              if(MediaController.countPause == 0 && countPlayTab == 0) {
                  try {
                      countPlayTab = 1;
                      countPauseTab = 0;
                      MediaController.berrier.await();
                      MediaController.mediaPlayer3.play();
                  }catch(Exception e) {};
              }
              if(MediaController.countSeek == 1) {
                  try {
                      MediaController.mediaPlayer3.pause();
                      MediaController.mediaPlayer3.seek(Duration.seconds(slider.getValue() / 100 * MediaController.mediaPlayer3.getTotalDuration().toSeconds()));
                      MediaController.countSeek = 0;
                      MediaController.berrier.await();
                      MediaController.mediaPlayer3.play();
                  }catch(Exception e) {};
              }
          }
      }
}



