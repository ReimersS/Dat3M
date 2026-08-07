package com.dat3m.dartagnan.program.processing;

import com.dat3m.dartagnan.program.Function;
import com.dat3m.dartagnan.program.Program;
import com.dat3m.dartagnan.program.event.Event;
import com.dat3m.dartagnan.program.event.metadata.lkmm.LkmmIntrinsic;
import com.dat3m.dartagnan.program.IRHelper;
import com.dat3m.dartagnan.program.processing.transformers.LiftLkmmIntrinsics;

import java.util.List;

public class LkmmIntrinsics implements ProgramProcessor {

    private LkmmIntrinsics() { }

    public static LkmmIntrinsics newInstance() {
        return new LkmmIntrinsics();
    }

    @Override
    public void run(Program program) {
        program.getFunctions().forEach(this::replace);
        program.getThreads().forEach(this::replace);
        IdReassignment.newInstance().run(program); // Reassign ids because of newly created events
    }

    private void replace(Function func) {
        func.getEvents().forEach(e -> replace(e));
    }

    private void replace(Event toBeRaplaced) {
        final Event pred = toBeRaplaced.getPredecessor();
        if (pred == null) {
            return; // We do not replace the entry event.
        }
        final List<Event> compiledEvents = toBeRaplaced.accept(new LiftLkmmIntrinsics());
        if (compiledEvents.size() == 1 && compiledEvents.get(0) == toBeRaplaced) {
            // In the special case where the replacement does nothing, we keep the event as is.
            return;
        }
        if (!toBeRaplaced.getUsers().isEmpty()) {
            final String error = String.format("Could not replace event '%d:  %s' because it is referenced by other events.",
                    toBeRaplaced.getGlobalId(), toBeRaplaced);
            throw new IllegalStateException(error);
        }
        IRHelper.replaceWithMetadata(toBeRaplaced, compiledEvents);
    }
    
}