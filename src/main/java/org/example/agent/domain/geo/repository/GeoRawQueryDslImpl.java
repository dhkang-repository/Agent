package org.example.agent.domain.geo.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.example.agent.entity.loc.GeoRawEntity;
import org.example.agent.entity.loc.QGeoRawEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;


@Slf4j
@Repository
public class GeoRawQueryDslImpl implements GeoRawQueryDsl {

    private final JPAQueryFactory jpaQueryFactory;

    @PersistenceContext(unitName = "agentEntityManager")
    private EntityManager entityManager;

    public GeoRawQueryDslImpl(@Autowired @Qualifier("agentJPAQueryFactory") JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }


    @Override
    public List<GeoRawEntity> search(Long userId,
                                     LocalDate from,
                                     LocalDate to) {
        QGeoRawEntity geoRawEntity = QGeoRawEntity.geoRawEntity;

        JPAQuery<GeoRawEntity> jpaQuery = jpaQueryFactory.selectFrom(geoRawEntity);

        BooleanBuilder builder = new BooleanBuilder();

        builder.and(geoRawEntity.id.eventDt.goe(from.atStartOfDay()));
        builder.and(geoRawEntity.id.eventDt.lt(to.atStartOfDay()));

        jpaQuery.where(builder).orderBy(geoRawEntity.id.eventDt.asc());

        return jpaQuery.fetch();
    }

}