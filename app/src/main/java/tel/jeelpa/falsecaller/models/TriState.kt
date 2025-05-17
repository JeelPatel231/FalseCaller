package tel.jeelpa.falsecaller.models

import arrow.core.Either
import arrow.core.Option

typealias TriState<T> = Option<Either<Throwable, T>>
