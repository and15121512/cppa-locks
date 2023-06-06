package sbt.cppa.locks;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class QNodeMCS {
    AtomicBoolean locked = new AtomicBoolean(false);
    final AtomicReference<QNodeMCS> next = null;
}

