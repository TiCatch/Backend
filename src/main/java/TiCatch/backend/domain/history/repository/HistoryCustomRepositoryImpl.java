package TiCatch.backend.domain.history.repository;

import TiCatch.backend.domain.history.dto.response.LevelHistoryResponse;
import TiCatch.backend.domain.history.dto.response.TicketingHistoryPagingResponse;
import TiCatch.backend.domain.ticketing.entity.TicketingLevel;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.List;
import java.util.Optional;

import static TiCatch.backend.domain.history.entity.QHistory.history;
import static ch.qos.logback.core.util.StringUtil.isNullOrEmpty;

@RequiredArgsConstructor
public class HistoryCustomRepositoryImpl implements HistoryCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Page<TicketingHistoryPagingResponse> findHistoryByUserIdWithPaged(Long userId, Pageable pageable) {
        List<TicketingHistoryPagingResponse> fetchResult = jpaQueryFactory.select(Projections.fields(TicketingHistoryPagingResponse.class,
                        history.historyId.as("historyId"),
                        history.user.userId.as("userId"),
                        history.ticketingId.as("ticketingId"),
                        history.seatInfo.as("seatInfo"),
                        history.ticketingScore.as("ticketingScore"),
                        history.ticketingLevel.as("ticketingLevel"),
                        history.ticketingTime.as("ticketingTime")))
                .from(history)
                .where(userIdEq(userId))
                .orderBy(getOrderSpecifier(pageable))
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .fetch();

        Long total = Optional.ofNullable(
                        jpaQueryFactory
                                .select(history.count())
                                .from(history)
                                .where(userIdEq(userId))
                                .fetchOne())
                .orElse(0L);

        return PageableExecutionUtils.getPage(fetchResult, pageable, () -> total);
    }

    @Override
    public LevelHistoryResponse findHistoryByUserIdWithLevelCounts(Long userId) {
        List<Tuple> result = jpaQueryFactory.select(history.ticketingLevel, history.count())
                .from(history)
                .where(userIdEq(userId))
                .groupBy(history.ticketingLevel)
                .fetch();

        long easyCount = 0;
        long normalCount = 0;
        long hardCount = 0;

        for (Tuple tuple : result) {
            TicketingLevel level = tuple.get(history.ticketingLevel);
            long count = tuple.get(history.count());
            if (level == TicketingLevel.EASY) {
                easyCount = count;
            } else if (level == TicketingLevel.NORMAL) {
                normalCount = count;
            } else if (level == TicketingLevel.HARD) {
                hardCount = count;
            }
        }

        return new LevelHistoryResponse(easyCount, normalCount, hardCount);
    }

    private BooleanExpression userIdEq(Long userId) {
        return isNullOrEmpty(String.valueOf(userId)) ? null : history.user.userId.eq(userId);
    }

    private OrderSpecifier<?> getOrderSpecifier(Pageable pageable) {
        for (Sort.Order order : pageable.getSort()) {
            if (order.getProperty().equals("ticketingTime")) {
                return order.isAscending() ? history.createdDate.asc() : history.createdDate.desc();
            } else if (order.getProperty().equals("ticketingScore")) {
                return order.isAscending() ? history.ticketingScore.asc() : history.ticketingScore.desc();
            }
        }
        return history.ticketingTime.desc();
    }
}