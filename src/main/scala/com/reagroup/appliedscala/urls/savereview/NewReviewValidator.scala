package com.reagroup.appliedscala.urls.savereview

import cats.data.Validated._
import cats.data.{Validated, ValidatedNel}
import cats.implicits._

object NewReviewValidator {

  /**
    * How do we combine two validations together? Refer to `ValidationExercises` for a refresher!
    *
    * Hint: `Validated` has an Applicative instance.
    */
  def validate(review: NewReviewRequest): ValidatedNel[ReviewValidationError, ValidatedReview] =
    (validateAuthor(review.author), validateComment(review.comment))
      .mapN((author, comment) => ValidatedReview(author, comment))

  /**
    * If `author` is empty, return an `InvalidNel` containing `ReviewAuthorTooShort`,
    * else return a `Valid` containing the `author`.
    *
    * Hint: You can use `.isEmpty` or `.nonEmpty` on `String`
    */
  private def validateAuthor(author: String): ValidatedNel[ReviewValidationError, String] =
    if (author.isEmpty) Validated.invalidNel(ReviewAuthorTooShort)
    else Validated.validNel(author)

  /**
    * If `comment` is empty, return an `InvalidNel` containing `ReviewCommentTooShort`,
    * else return a `Valid` containing the `comment`.
    *
    * Hint: You can use `.isEmpty` or `.nonEmpty` on `String`
    */
  private def validateComment(comment: String): ValidatedNel[ReviewValidationError, String] =
    if (comment.isEmpty) Validated.invalidNel(ReviewCommentTooShort)
    else Validated.validNel(comment)


}
