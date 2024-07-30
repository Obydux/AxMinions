package com.artillexstudios.axminions.minions.actions.collectors;

import com.artillexstudios.axminions.minions.Minion;
import com.artillexstudios.axminions.minions.actions.filters.Filter;
import com.artillexstudios.axminions.minions.actions.filters.Filters;
import com.artillexstudios.axminions.utils.LogUtils;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import redempt.crunch.Crunch;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

public abstract class Collector<T> {

    public static Collector<?> of(Map<Object, Object> config) {
        LogUtils.debug("Collector of {}", config);
        if (config == null) {
            return null;
        }

        String collectorID = (String) config.get("id");
        if (collectorID == null) {
            LogUtils.warn("Collector id was not defined!");
            return null;
        }

        if (!CollectorRegistry.exists(collectorID)) {
            LogUtils.warn("Collector id {} not present! Collector ids: {}", collectorID, CollectorRegistry.keys());
            return null;
        }

        String shape = (String) config.get("shape");
        if (shape == null) {
            LogUtils.warn("Shape was not defined!");
            return null;
        }

        Optional<CollectorShape> collectorShape = CollectorShape.parse(shape);
        if (collectorShape.isEmpty()) {
            LogUtils.warn("Invalid shape! Shapes: {}", Arrays.toString(CollectorShape.entries));
            return null;
        }

        String radius = (String) config.get("radius");
        if (radius == null) {
            LogUtils.warn("Radius was not defined!");
            return null;
        }

        List<Map<Object, Object>> filterMap = (List<Map<Object, Object>>) config.get("filters");
        List<Filter<?>> filters = new ObjectArrayList<>(1);
        if (filterMap != null) {
            for (Map<Object, Object> map : filterMap) {
                String filterId = (String) map.get("id");

                if (filterId == null) {
                    LogUtils.warn("Could not find id in filter config!");
                    continue;
                }

                Filter<?> filter = Filters.parse(filterId, map);
                if (filter == null) {
                    LogUtils.warn("Could not find filter with id {}!", filterId);
                    continue;
                }

                filters.add(filter);
            }
        }

        return CollectorRegistry.get(collectorID, collectorShape.get(), Crunch.compileExpression(radius.replace("<level>", "$1")), filters);
    }

    public abstract Class<?> getCollectedClass();

    public abstract void collect(Minion minion, Consumer<T> consumer);
}
