// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

package fi.hsl.parkandride.back;

import java.util.List;

import com.querydsl.core.Tuple;
import com.querydsl.core.dml.StoreClause;
import com.querydsl.sql.RelationalPathBase;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.MappingProjection;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.StringPath;

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
