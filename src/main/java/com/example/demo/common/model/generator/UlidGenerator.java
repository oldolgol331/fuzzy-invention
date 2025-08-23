package com.example.demo.common.model.generator;

import com.github.f4b6a3.ulid.UlidCreator;
import java.io.Serializable;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

/**
 * PackageName : com.example.demo.common.model.generator
 * FileName    : UlidGenerator
 * Author      : oldolgol331
 * Date        : 25. 8. 23.
 * Description :
 * =====================================================================================================================
 * DATE          AUTHOR               DESCRIPTION
 * ---------------------------------------------------------------------------------------------------------------------
 * 25. 8. 23.    oldolgol331          Initial creation
 */
public class UlidGenerator implements IdentifierGenerator {

    @Override
    public Serializable generate(SharedSessionContractImplementor session, Object object) throws HibernateException {
        return UlidCreator.getUlid().toUuid();
    }

}
