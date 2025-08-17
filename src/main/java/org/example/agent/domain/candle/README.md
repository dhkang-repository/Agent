## 전체 목표 및 데이터 흐름

1. **DB**: `tbl_coin_day_candle`
   - 시장
   - 날짜
   - 시가/고가/저가/종가
   - 거래대금/거래량
2. **Repository**
   - 날짜 구간 오름차순으로 캔들 로드
3. **Indicator (보조지표 계산)**
   - SMA(이평)
   - RSI
   - ATR14 
4. **Strategy**:
    - **매수**
      - `SMA20`이 `SMA50` **상향돌파(골든크로스)** AND `RSI14 < 60`
    - **청산**
      - 데드크로스 OR `ATR*2` 손절 OR **2R** 익절
5. **Backtest**
   - 과거 구간 순회로 진입/청산 시뮬레이션 
   - → 거래 리스트, 총손익, 승률, 최대낙폭(MDD)
6. **Service + Controller**:
    - `/strategy/signal` : 마지막 일자 기준 BUY / SELL / HOLD + 이유
    - `/strategy/backtest` : 구간 백테스트 결과

> **워밍업 구간** 필요
>> SMA/RSI/ATR 초기 계산을 위해 `startDate` 이전 **약 60일**을 추가 로드하면 경계 왜곡이 줄어듭니다.


## DB (Entity)
### 테이블 스키마
- 유니크 제약
  - `UNIQUE (market, candle_date)`
- 정밀도
  - 가격: `DECIMAL(24,8)`
  - 거래량/거래대금: `DECIMAL(30,8)` 
- 타임존
  - `candle_date`: **KST 기준 yyyy-MM-dd**

