package TiCatch.backend.domain.history.repository;

import TiCatch.backend.domain.history.dto.response.HistoryPagingResponse;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.List;
import java.util.Optional;

import static TiCatch.backend.domain.history.entity.QHistory.history;
import static ch.qos.logback.core.util.StringUtil.isNullOrEmpty;

@RequiredArgsConstructor
public class HistoryCustomRepositoryImpl implements HistoryCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Page<HistoryPagingResponse> findHistoryByUserIdWithPaged(Long userId, Pageable pageable) {
        List<HistoryPagingResponse> fetchResult = jpaQueryFactory.select(Projections.fields(HistoryPagingResponse.class,
                        history.historyId.as("historyId"),
                        history.user.userId.as("userId"),
                        history.ticketingId.as("ticketingId"),
                        history.seatInfo.as("seatInfo"),
                        history.ticketingLevel.as("ticketingLevel"),
                        history.ticketingTime.as("ticketingTime")))
                .from(history)
                .where(userIdEq(userId))
                .orderBy(history.createdDate.desc())
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

    private BooleanExpression userIdEq(Long userId) {
        return isNullOrEmpty(String.valueOf(userId)) ? null : history.user.userId.eq(userId);
    }
}