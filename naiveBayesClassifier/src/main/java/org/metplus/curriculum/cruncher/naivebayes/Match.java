package org.metplus.curriculum.cruncher.naivebayes;

import java.util.List;

public interface Match<Entry, Result> {
    List<Result> match(Entry entry);
    List<Entry> matchInverse(Result entry);
}
