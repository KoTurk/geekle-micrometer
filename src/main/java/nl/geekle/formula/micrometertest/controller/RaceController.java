package nl.geekle.formula.micrometertest.controller;

import io.micrometer.core.annotation.Timed;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import nl.geekle.formula.micrometertest.domain.FormulaOneCar;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("car/race")
public class RaceController {
    PrometheusMeterRegistry registry;
    FormulaOneCar formulaOneCar;

    public RaceController(PrometheusMeterRegistry registry, FormulaOneCar formulaOneCar) {
        this.registry = registry;
        this.formulaOneCar = formulaOneCar;
    }

    @RequestMapping("")
    @Timed(value = "car.race.drive.longtasktimer", longTask = true)
    public String race() throws InterruptedException {
        formulaOneCar.driving();
        return "we're racing";
    }

    @RequestMapping("pitstop")
    //@Timed("car.race.pitstop.timer")
    public String havePitstop() throws InterruptedException {
        Metrics.timer("car.race.pitstop.timer").record(formulaOneCar.refuel());
     //   Timer.builder("car.race.pitstop.timer").tags("MaxVerstappen").register(registry).record(formulaOneCar.refuel());
        formulaOneCar.refuelAndWait();
        return "pitstop done";
    }

    @RequestMapping("/laps")
    public String addOneLap() {
        Metrics.counter("car.race.laps.counter").increment();
        return "Added a lap";
    }

    @RequestMapping("/tires")
    public void getTireWear() {
        Metrics.gauge("car.race.tire.wear.gauge", formulaOneCar.getTireWear());
    }
}
