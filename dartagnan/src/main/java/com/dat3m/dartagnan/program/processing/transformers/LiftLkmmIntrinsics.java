package com.dat3m.dartagnan.program.processing.transformers;

import com.dat3m.dartagnan.program.event.Event;
import com.dat3m.dartagnan.program.event.Tag;
import com.dat3m.dartagnan.program.event.core.*;
import com.dat3m.dartagnan.program.event.functions.*;
import com.dat3m.dartagnan.program.event.EventVisitor;
import com.dat3m.dartagnan.program.event.lang.dat3m.NonDetChoice;
import com.dat3m.dartagnan.program.event.metadata.lkmm.*;

import java.util.List;
import java.util.Collections;

import static com.dat3m.dartagnan.program.event.EventFactory.*;

public class LiftLkmmIntrinsics implements EventVisitor<List<Event>> {

    public LiftLkmmIntrinsics() {}

    @Override
    public List<Event> visitEvent(Event e) {
        return Collections.singletonList(e);
    }

    @Override
    public List<Event> visitNonDetChoice(NonDetChoice e) {
        if (!e.hasMetadata(LkmmIntrinsic.class)) {
            return Collections.singletonList(e);
        }
        String metadata = e.getMetadata(LkmmIntrinsic.class).name();
        if (metadata.equals("mb")) {
            return Collections.singletonList(Linux.newLKMMFence(Tag.Linux.MO_MB));
        }
        if (metadata.equals("rmb")) {
            return Collections.singletonList(Linux.newLKMMFence(Tag.Linux.MO_RMB));
        }
        if (metadata.equals("wmb")) {
            return Collections.singletonList(Linux.newLKMMFence(Tag.Linux.MO_WMB));
        }
        return Collections.singletonList(e);
    }

    @Override
    public List<Event> visitLoad(Load e) {
        if (!e.hasMetadata(LkmmIntrinsic.class)) {
            return Collections.singletonList(e);
        }
        String metadata = e.getMetadata(LkmmIntrinsic.class).name();
        if (metadata.equals("READ_ONCE")) {
            return Collections.singletonList(addDepencyTags(e, Linux.newLKMMLoad(e.getResultRegister(), e.getAddress(), Tag.Linux.MO_ONCE)));
        }
        return Collections.singletonList(e);
    }

    @Override
    public List<Event> visitStore(Store e) {
        if (!e.hasMetadata(LkmmIntrinsic.class)) {
            return Collections.singletonList(e);
        }
        String metadata = e.getMetadata(LkmmIntrinsic.class).name();
        if (metadata.equals("WRITE_ONCE")) {
            return Collections.singletonList(addDepencyTags(e, Linux.newLKMMStore(e.getAddress(), e.getMemValue(), Tag.Linux.MO_ONCE)));
        }
        return Collections.singletonList(e);
    }

    @Override
    public List<Event> visitGenericVisibleEvent(GenericVisibleEvent e) {
        if (!e.hasMetadata(LkmmIntrinsic.class)) {
            return Collections.singletonList(e);
        }
        String metadata = e.getMetadata(LkmmIntrinsic.class).name();
        if (metadata.equals("mb")) {
            return Collections.singletonList(Linux.newLKMMFence(Tag.Linux.MO_MB));
        }
        if (metadata.equals("rmb")) {
            return Collections.singletonList(Linux.newLKMMFence(Tag.Linux.MO_RMB));
        }
        if (metadata.equals("wmb")) {
            return Collections.singletonList(Linux.newLKMMFence(Tag.Linux.MO_WMB));
        }
        return Collections.singletonList(e);
    }

    private Event addDepencyTags(Event s, Event t) {
        if (s.hasMetadata(Dependency.class)) {
            t.addTags(s.getMetadata(Dependency.class).start() ? Tag.Linux.DEP_BEGINGS : Tag.Linux.DEP_ENDS);
        }
        return t;
    }
}
