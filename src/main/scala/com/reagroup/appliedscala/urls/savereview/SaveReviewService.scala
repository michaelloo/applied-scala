package com.reagroup.appliedscala.urls.savereview

import cats.data.{NonEmptyList, Validated, ValidatedNel}
import cats.effect.IO
import cats.implicits._
import com.reagroup.appliedscala.models.{Movie, MovieId}

class SaveReviewService(saveReview: (MovieId, ValidatedReview) => IO[ReviewId],
                        fetchMovie: MovieId => IO[Option[Movie]]) {

  /**
    * Before saving a `NewReviewRequest`, we want to check that the movie exists and then
    * validate the request in order to get a `ValidatedReview`.
    * Complete `NewReviewValidator`, then use it here before calling `saveReview`.
    * Return all errors encountered whether the movie exists or not.
    *
    */
  def save(movieId: MovieId, review: NewReviewRequest): IO[ValidatedNel[ReviewValidationError, ReviewId]] = for {
    errorOrMovie <- fetchMovie(movieId).attempt
    movie <- errorOrMovie match {
      case Left(error) => IO.raiseError(error)
      case Right(movie) => IO.pure(validateMovie(movie))
    }
    validMovieAndReview <- movie.productR(validateReview(review)) match {
      case Validated.Invalid(errors) => IO.pure(Validated.Invalid(errors))
      case Validated.Valid(validatedReview) => saveReview(movieId, validatedReview).map(Validated.validNel)
    }
  } yield validMovieAndReview

  private def validateMovie(movieOp: Option[Movie]): ValidatedNel[ReviewValidationError, Movie] =
    movieOp match {
      case Some(value) => Validated.validNel(value)
      case None => Validated.invalidNel(MovieDoesNotExist)
    }

  private def validateReview(review: NewReviewRequest): ValidatedNel[ReviewValidationError, ValidatedReview] =
    NewReviewValidator.validate(review)

}
