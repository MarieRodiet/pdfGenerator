package mariemoore.PDFGenerator.util;

import io.micrometer.common.util.StringUtils;

import java.time.Duration;
import java.time.LocalDateTime;

public class Timer {

    private String name;
    private LocalDateTime start;
    private LocalDateTime end;

    private Duration elapsedTime;

    public Timer(String timerName){

        this.name = StringUtils.isBlank(timerName) ? "timer" : timerName;
        reset();
    }

    public void start(){
        this.start = LocalDateTime.now();
    }

    public void end(){
        end = LocalDateTime.now();
        elapsedTime = Duration.between(start, end);
    }

    public void reset(){
        start = null;
        end = null;
    }

    public String format(){
        String hms = String.format("%s => %d h %02d m %02d s. %03d", this.name, elapsedTime.toHours(), elapsedTime.toMinutesPart(), elapsedTime.toSecondsPart(), elapsedTime.toMillisPart());
        return hms;
    }
}
