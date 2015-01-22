package fi.hsl.parkandride.back;

import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import com.mysema.query.group.AbstractGroupExpression;
import com.mysema.query.group.GroupCollector;
import com.mysema.query.types.Expression;

public class GSortedSet<T> extends AbstractGroupExpression<T, SortedSet<T>> {

    public static <E> AbstractGroupExpression<E, SortedSet<E>> sortedSet(Expression<E> expression) {
        return new GSortedSet<E>(expression);
    }

    private static final long serialVersionUID = -1575808026237160843L;

    public GSortedSet(Expression<T> expr) {
        super(Set.class, expr);
    }

    @Override
    public GroupCollector<T,SortedSet<T>> createGroupCollector() {
        return new GroupCollector<T,SortedSet<T>>() {

            private final TreeSet<T> set = new TreeSet<T>();

            @Override
            public void add(T o) {
                if (o != null) {
                    set.add(o);
                }
            }

            @Override
            public SortedSet<T> get() {
                return set;
            }

        };
    }
}
