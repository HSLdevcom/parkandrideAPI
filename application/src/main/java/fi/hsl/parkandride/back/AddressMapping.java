package fi.hsl.parkandride.back;

import java.util.List;

import com.mysema.query.Tuple;
import com.mysema.query.dml.StoreClause;
import com.mysema.query.sql.RelationalPathBase;
import com.mysema.query.types.Expression;
import com.mysema.query.types.MappingProjection;
import com.mysema.query.types.Path;
import com.mysema.query.types.path.StringPath;

import fi.hsl.parkandride.core.domain.Address;

class AddressMapping extends MappingProjection<Address> {

    private final MultilingualStringMapping streetAddress;

    private final Path<String> postalCode;

    private final MultilingualStringMapping city;

    public AddressMapping(RelationalPathBase<?> owner) {
        super(Address.class, createExpressions(owner));
        List<Expression<?>> args = getArgs();
        this.streetAddress = (MultilingualStringMapping) args.get(0);
        this.postalCode = (Path<String>) args.get(1);
        this.city = (MultilingualStringMapping) args.get(2);
    }

    private static Expression<?>[] createExpressions(RelationalPathBase<?> table) {
        Path<?>[] paths = table.all();
        return new Expression<?>[] {
                new MultilingualStringMapping(
                        findPath(paths, "streetAddressFi"),
                        findPath(paths, "streetAddressSv"),
                        findPath(paths, "streetAddressEn")),
                findPath(paths, "postalCode"),
                new MultilingualStringMapping(
                        findPath(paths, "cityFi"),
                        findPath(paths, "citySv"),
                        findPath(paths, "cityEn"))
        };
    }

    private static StringPath findPath(Path<?>[] paths, String name) {
        for (Path<?> path : paths) {
            if (path.getMetadata().getName().equals(name)) {
                return (StringPath) path;
            }
        }
        throw new IllegalArgumentException("Path not found: " + name);
    }

    @Override
    protected Address map(Tuple row) {
        Address address = new Address();
        address.streetAddress = streetAddress.map(row);
        address.postalCode = row.get(postalCode);
        address.city = city.map(row);
        return address;
    }

    protected void populate(Address address, StoreClause<?> store) {
        if (address == null) {
            address = new Address();
        }
        streetAddress.populate(address.streetAddress, store);
        store.set(postalCode, address.postalCode);
        city.populate(address.city, store);
    }

}
