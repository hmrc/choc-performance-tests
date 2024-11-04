/*
 * Copyright 2024 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc.perftests

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.http.request.builder.HttpRequestBuilder
import uk.gov.hmrc.performance.conf.ServicesConfiguration

object Requests extends ServicesConfiguration {

  val baseUrl: String = baseUrlFor("bank-account-coc-frontend")
  val authUrl: String = baseUrlFor("auth-login")
  val route: String   = "/change-bank-account"

  val getAuthLoginPage: HttpRequestBuilder =
    http("Get Auth Page")
      .get(s"$authUrl/auth-login-stub/gg-sign-in")
      .check(status.is(200))

  val postAuthLoginPage: HttpRequestBuilder =
    http("Post Auth Page")
      .post(s"$authUrl/auth-login-stub/gg-sign-in")
      .formParam("authorityId", "")
      .formParam("redirectionUrl", s"$baseUrl$route/test-only/start-journey")
      .formParam("credentialStrength", "strong")
      .formParam("confidenceLevel", "50")
      .formParam("affinityGroup", "Individual")
      .formParam("enrolment[0].name", "HMRC-MTD-VAT")
      .formParam("enrolment[0].taxIdentifier[0].name", "VRN")
      .formParam("enrolment[0].taxIdentifier[0].value", "101747696")
      .formParam("enrolment[0].state", "Activated")
      .check(status.is(303))
      .check(header("Location").is(s"$baseUrl$route/test-only/start-journey").saveAs("StartPage"))

  val getStartPage: HttpRequestBuilder =
    http("Get Start Page")
      .get("${StartPage}": String)
      .check(status.is(200))
      .check(css("input[name=csrfToken]", "value").saveAs("csrfToken"))

  val postStartPage: HttpRequestBuilder =
    http("Post Start Page")
      .post("${StartPage}": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("vrn", "101747696")
      .formParam("isAgent", "false")
      .formParam("returnUrl", s"$baseUrl/$route/test-only/show-return-url")
      .formParam("backUrl", s"$baseUrl/$route/test-only/show-back-url")
      .formParam("convenienceUrl", s"$baseUrl/$route/test-only/show_convenience_url")
      .formParam("partyType", "")
      .check(status.is(303))
      .check(header("Location").saveAs("EnterDetailsPage"))

  val getEnterDetailsPage: HttpRequestBuilder =
    http("Get Enter Details Page")
      .get("${EnterDetailsPage}": String)
      .check(status.is(200))
      .check(css("input[name=csrfToken]", "value").saveAs("csrfToken"))

  val postEnterDetailsPage: HttpRequestBuilder =
    http("Post Enter Details Page")
      .post("${EnterDetailsPage}": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("accountName", "Melvin Loper")
      .formParam("sortCode", "207106")
      .formParam("accountNumber", "44311677")
      .check(status.is(303))
      .check(header("Location").saveAs("ConfirmDetailsPage"))

  val getConfirmDetailsPage: HttpRequestBuilder =
    http("Get Confirm Details Page")
      .get(s"$baseUrl$${ConfirmDetailsPage}": String)
      .check(status.is(200))
      .check(css("input[name=csrfToken]", "value").saveAs("csrfToken"))

  val postConfirmDetailsPage: HttpRequestBuilder =
    http("Post Confirm Details Page")
      .post(s"$baseUrl$${ConfirmDetailsPage}": String)
      .formParam("csrfToken", "${csrfToken}")
      .check(status.is(303))
      .check(header("Location").saveAs("CompletionPage"))

  val getCompletionPage: HttpRequestBuilder =
    http("Get Completion Page")
      .get(s"$baseUrl$${CompletionPage}": String)
      .check(status.is(200))
}
