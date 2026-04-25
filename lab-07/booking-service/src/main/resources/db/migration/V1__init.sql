CREATE TABLE bookings (
    id              VARCHAR(40)    PRIMARY KEY,
    user_id         VARCHAR(40)    NOT NULL,
    movie_id        VARCHAR(40)    NOT NULL,
    showtime        TIMESTAMPTZ    NOT NULL,
    seats           VARCHAR(500)   NOT NULL,
    unit_price      NUMERIC(12,2)  NOT NULL,
    amount          NUMERIC(12,2)  NOT NULL,
    status          VARCHAR(20)    NOT NULL,
    failed_reason   VARCHAR(200),
    created_at      TIMESTAMPTZ    NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ    NOT NULL DEFAULT now()
);

CREATE INDEX idx_bookings_user_created   ON bookings (user_id, created_at DESC);
CREATE INDEX idx_bookings_movie_showtime ON bookings (movie_id, showtime);
CREATE INDEX idx_bookings_status         ON bookings (status);

CREATE TABLE processed_events (
    event_id      VARCHAR(60)  PRIMARY KEY,
    event_type    VARCHAR(50)  NOT NULL,
    processed_at  TIMESTAMPTZ  NOT NULL DEFAULT now()
);
