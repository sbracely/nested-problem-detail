# WebMVC Example Application Exception Response Table

- Data source: response bodies come from the current `3.x` branch `openapi.json` examples that are kept aligned with runtime by contract tests; controller-only scenarios are added from the current controller tests.
- Ordering: follows the current `ResponseEntityExceptionHandler#handleException` dispatch order on the `3.x` line; subclasses are placed next to their parent class.
- Matrix-variable-related cases are listed separately in 2 rows: `MissingMatrixVariableException` and the matrix-variable validation-failure case for `HandlerMethodValidationException`.
- `asyncRequestNotUsableException` records the normal client-visible response (`200 text/event-stream`); the exception itself is only triggered after the client disconnects/times out and the server writes again.

<table>
  <thead>
    <tr><th>No.</th><th>Scenario</th><th>Response</th></tr>
  </thead>
  <tbody>
        <tr>
      <td>1</td>
      <td><code>org.springframework.web.HttpRequestMethodNotSupportedException</code><br><code>POST /mvc-extended-problem-detail/http-request-method-not-supported-exception</code><br><pre>None</pre></td>
      <td><pre>status: 405
Content-Type: application/problem+json
Allow: GET

{
  &quot;type&quot;: &quot;about:blank&quot;,
  &quot;title&quot;: &quot;Method Not Allowed&quot;,
  &quot;status&quot;: 405,
  &quot;detail&quot;: &quot;Method &#39;POST&#39; is not supported.&quot;,
  &quot;instance&quot;: &quot;/mvc-extended-problem-detail/http-request-method-not-supported-exception&quot;
}</pre></td>
    </tr>
        <tr>
      <td>2</td>
      <td><code>org.springframework.web.HttpMediaTypeNotSupportedException</code><br><code>PUT /mvc-extended-problem-detail/http-media-type-not-supported-exception</code><br><pre>None (no Content-Type sent)</pre></td>
      <td><pre>status: 415
Content-Type: application/problem+json
Accept: application/json

{
  &quot;type&quot;: &quot;about:blank&quot;,
  &quot;title&quot;: &quot;Unsupported Media Type&quot;,
  &quot;status&quot;: 415,
  &quot;detail&quot;: &quot;Content-Type &#39;null&#39; is not supported.&quot;,
  &quot;instance&quot;: &quot;/mvc-extended-problem-detail/http-media-type-not-supported-exception&quot;
}</pre></td>
    </tr>
        <tr>
      <td>3</td>
      <td><code>org.springframework.web.HttpMediaTypeNotAcceptableException</code><br><code>PUT /mvc-extended-problem-detail/http-media-type-not-acceptable-exception</code><br><pre>Header: Accept=application/xml</pre></td>
      <td><pre>status: 406
Content-Type: application/problem+json
Accept: application/json

{
  &quot;type&quot;: &quot;about:blank&quot;,
  &quot;title&quot;: &quot;Not Acceptable&quot;,
  &quot;status&quot;: 406,
  &quot;detail&quot;: &quot;Acceptable representations: [application/json].&quot;,
  &quot;instance&quot;: &quot;/mvc-extended-problem-detail/http-media-type-not-acceptable-exception&quot;
}</pre></td>
    </tr>
        <tr>
      <td>4</td>
      <td><code>org.springframework.web.bind.MissingPathVariableException</code><br><code>DELETE /mvc-extended-problem-detail/missing-path-variable-exception</code><br><pre>None</pre></td>
      <td><pre>status: 500
Content-Type: application/problem+json

{
  &quot;type&quot;: &quot;about:blank&quot;,
  &quot;title&quot;: &quot;Internal Server Error&quot;,
  &quot;status&quot;: 500,
  &quot;detail&quot;: &quot;Required path variable &#39;id&#39; is not present.&quot;,
  &quot;instance&quot;: &quot;/mvc-extended-problem-detail/missing-path-variable-exception&quot;
}</pre></td>
    </tr>
        <tr>
      <td>5</td>
      <td><code>org.springframework.web.bind.MissingServletRequestParameterException</code><br><code>GET /mvc-extended-problem-detail/missing-servlet-request-parameter-exception</code><br><pre>None (missing query parameter id)</pre></td>
      <td><pre>status: 400
Content-Type: application/problem+json

{
  &quot;type&quot;: &quot;about:blank&quot;,
  &quot;title&quot;: &quot;Bad Request&quot;,
  &quot;status&quot;: 400,
  &quot;detail&quot;: &quot;Required parameter &#39;id&#39; is not present.&quot;,
  &quot;instance&quot;: &quot;/mvc-extended-problem-detail/missing-servlet-request-parameter-exception&quot;
}</pre></td>
    </tr>
        <tr>
      <td>6</td>
      <td><code>org.springframework.web.multipart.support.MissingServletRequestPartException</code><br><code>PUT /mvc-extended-problem-detail/missing-servlet-request-part-exception</code><br><pre>multipart/form-data; missing file part</pre></td>
      <td><pre>status: 400
Content-Type: application/problem+json

{
  &quot;type&quot;: &quot;about:blank&quot;,
  &quot;title&quot;: &quot;Bad Request&quot;,
  &quot;status&quot;: 400,
  &quot;detail&quot;: &quot;Required part &#39;file&#39; is not present.&quot;,
  &quot;instance&quot;: &quot;/mvc-extended-problem-detail/missing-servlet-request-part-exception&quot;
}</pre></td>
    </tr>
        <tr>
      <td>7</td>
      <td><code>org.springframework.web.bind.ServletRequestBindingException</code><br><code>GET /mvc-extended-problem-detail/servlet-request-binding-exception</code><br><pre>None</pre></td>
      <td><pre>status: 400
Content-Type: application/problem+json

{
  &quot;type&quot;: &quot;about:blank&quot;,
  &quot;title&quot;: &quot;Bad Request&quot;,
  &quot;status&quot;: 400,
  &quot;instance&quot;: &quot;/mvc-extended-problem-detail/servlet-request-binding-exception&quot;
}</pre></td>
    </tr>
        <tr>
      <td>8</td>
      <td><code>org.springframework.web.bind.UnsatisfiedServletRequestParameterException</code><br><sub>extends ServletRequestBindingException</sub><br><code>GET /mvc-extended-problem-detail/unsatisfied-servlet-request-parameter-exception</code><br><pre>Query: type=1 (does not satisfy exist and !debug)</pre></td>
      <td><pre>status: 400
Content-Type: application/problem+json

{
  &quot;type&quot;: &quot;about:blank&quot;,
  &quot;title&quot;: &quot;Bad Request&quot;,
  &quot;status&quot;: 400,
  &quot;detail&quot;: &quot;Invalid request parameters.&quot;,
  &quot;instance&quot;: &quot;/mvc-extended-problem-detail/unsatisfied-servlet-request-parameter-exception&quot;
}</pre></td>
    </tr>
        <tr>
      <td>9</td>
      <td><code>org.springframework.web.bind.MissingRequestValueException</code><br><sub>extends ServletRequestBindingException</sub><br><code>GET /mvc-extended-problem-detail/org-spring-web-bind-missing-request-value-exception</code><br><pre>None</pre></td>
      <td><pre>status: 400
Content-Type: application/problem+json

{
  &quot;type&quot;: &quot;about:blank&quot;,
  &quot;title&quot;: &quot;Bad Request&quot;,
  &quot;status&quot;: 400,
  &quot;instance&quot;: &quot;/mvc-extended-problem-detail/org-spring-web-bind-missing-request-value-exception&quot;
}</pre></td>
    </tr>
        <tr>
      <td>10</td>
      <td><code>org.springframework.web.bind.MissingMatrixVariableException</code><br><sub>extends MissingRequestValueException → ServletRequestBindingException</sub><br><code>GET /mvc-extended-problem-detail/missing-matrix-variable-exception/abc;list1=a,b,c</code><br><pre>Path matrix: list1=a,b,c (intentionally missing list)</pre></td>
      <td><pre>status: 400
Content-Type: application/problem+json

{
  &quot;type&quot;: &quot;about:blank&quot;,
  &quot;title&quot;: &quot;Bad Request&quot;,
  &quot;status&quot;: 400,
  &quot;detail&quot;: &quot;Required path parameter &#39;list&#39; is not present.&quot;,
  &quot;instance&quot;: &quot;/mvc-extended-problem-detail/missing-matrix-variable-exception/abc;list1=a,b,c&quot;
}</pre></td>
    </tr>
        <tr>
      <td>11</td>
      <td><code>org.springframework.web.bind.MissingRequestCookieException</code><br><sub>extends MissingRequestValueException → ServletRequestBindingException</sub><br><code>GET /mvc-extended-problem-detail/missing-request-cookie-exception</code><br><pre>None (missing cookie `cookieValue`)</pre></td>
      <td><pre>status: 400
Content-Type: application/problem+json

{
  &quot;type&quot;: &quot;about:blank&quot;,
  &quot;title&quot;: &quot;Bad Request&quot;,
  &quot;status&quot;: 400,
  &quot;detail&quot;: &quot;Required cookie &#39;cookieValue&#39; is not present.&quot;,
  &quot;instance&quot;: &quot;/mvc-extended-problem-detail/missing-request-cookie-exception&quot;
}</pre></td>
    </tr>
        <tr>
      <td>12</td>
      <td><code>org.springframework.web.bind.MissingRequestHeaderException</code><br><sub>extends MissingRequestValueException → ServletRequestBindingException</sub><br><code>GET /mvc-extended-problem-detail/missing-request-header-exception</code><br><pre>None (missing request header `header`)</pre></td>
      <td><pre>status: 400
Content-Type: application/problem+json

{
  &quot;type&quot;: &quot;about:blank&quot;,
  &quot;title&quot;: &quot;Bad Request&quot;,
  &quot;status&quot;: 400,
  &quot;detail&quot;: &quot;Required header &#39;header&#39; is not present.&quot;,
  &quot;instance&quot;: &quot;/mvc-extended-problem-detail/missing-request-header-exception&quot;
}</pre></td>
    </tr>
        <tr>
      <td>13</td>
      <td><code>org.springframework.web.bind.MethodArgumentNotValidException</code><br><code>POST /mvc-extended-problem-detail/method-argument-not-valid-exception</code><br><pre>Content-Type: application/json; Body: {&quot;name&quot;:&quot;abc&quot;,&quot;password&quot;:&quot;123&quot;}</pre></td>
      <td><pre>status: 400
Content-Type: application/problem+json

{
  &quot;type&quot;: &quot;about:blank&quot;,
  &quot;title&quot;: &quot;Bad Request&quot;,
  &quot;status&quot;: 400,
  &quot;detail&quot;: &quot;Invalid request content.&quot;,
  &quot;instance&quot;: &quot;/mvc-extended-problem-detail/method-argument-not-valid-exception&quot;,
  &quot;errors&quot;: [
    {
      &quot;type&quot;: &quot;PARAMETER&quot;,
      &quot;target&quot;: &quot;name&quot;,
      &quot;message&quot;: &quot;Name length must be between 6-10&quot;
    },
    {
      &quot;type&quot;: &quot;PARAMETER&quot;,
      &quot;target&quot;: &quot;age&quot;,
      &quot;message&quot;: &quot;Age cannot be null&quot;
    },
    {
      &quot;type&quot;: &quot;PARAMETER&quot;,
      &quot;target&quot;: &quot;password&quot;,
      &quot;message&quot;: &quot;Password and confirm password do not match&quot;
    },
    {
      &quot;type&quot;: &quot;PARAMETER&quot;,
      &quot;target&quot;: &quot;confirmPassword&quot;,
      &quot;message&quot;: &quot;Password and confirm password do not match&quot;
    }
  ]
}</pre></td>
    </tr>
        <tr>
      <td>14</td>
      <td><code>org.springframework.web.method.annotation.HandlerMethodValidationException</code><br><sub>extends ResponseStatusException → ErrorResponseException</sub><br><code>GET /mvc-extended-problem-detail/handler-method-validation-exception-cookie-value</code><br><pre>Cookie: name=a</pre></td>
      <td><pre>status: 400
Content-Type: application/problem+json

{
  &quot;type&quot;: &quot;about:blank&quot;,
  &quot;title&quot;: &quot;Bad Request&quot;,
  &quot;status&quot;: 400,
  &quot;detail&quot;: &quot;Validation failure&quot;,
  &quot;instance&quot;: &quot;/mvc-extended-problem-detail/handler-method-validation-exception-cookie-value&quot;,
  &quot;errors&quot;: [
    {
      &quot;type&quot;: &quot;COOKIE&quot;,
      &quot;target&quot;: &quot;name&quot;,
      &quot;message&quot;: &quot;Name length must be at least 2&quot;
    }
  ]
}</pre></td>
    </tr>
        <tr>
      <td>15</td>
      <td><code>org.springframework.web.method.annotation.HandlerMethodValidationException</code><br><sub>extends ResponseStatusException → ErrorResponseException</sub><br><code>GET /mvc-extended-problem-detail/handler-method-validation-exception-matrix-variable/abc;list=a,b,c</code><br><pre>Path matrix: list=a,b,c</pre></td>
      <td><pre>status: 400
Content-Type: application/problem+json

{
  &quot;type&quot;: &quot;about:blank&quot;,
  &quot;title&quot;: &quot;Bad Request&quot;,
  &quot;status&quot;: 400,
  &quot;detail&quot;: &quot;Validation failure&quot;,
  &quot;instance&quot;: &quot;/mvc-extended-problem-detail/handler-method-validation-exception-matrix-variable/abc;list=a,b,c&quot;,
  &quot;errors&quot;: [
    {
      &quot;type&quot;: &quot;PARAMETER&quot;,
      &quot;target&quot;: &quot;list&quot;,
      &quot;message&quot;: &quot;Maximum size is 2&quot;
    }
  ]
}</pre></td>
    </tr>
        <tr>
      <td>16</td>
      <td><code>org.springframework.web.method.annotation.HandlerMethodValidationException</code><br><sub>extends ResponseStatusException → ErrorResponseException</sub><br><code>GET /mvc-extended-problem-detail/handler-method-validation-exception-model-attribute</code><br><pre>None</pre></td>
      <td><pre>status: 400
Content-Type: application/problem+json

{
  &quot;type&quot;: &quot;about:blank&quot;,
  &quot;title&quot;: &quot;Bad Request&quot;,
  &quot;status&quot;: 400,
  &quot;detail&quot;: &quot;Validation failure&quot;,
  &quot;instance&quot;: &quot;/mvc-extended-problem-detail/handler-method-validation-exception-model-attribute&quot;,
  &quot;errors&quot;: [
    {
      &quot;type&quot;: &quot;PARAMETER&quot;,
      &quot;target&quot;: &quot;password&quot;,
      &quot;message&quot;: &quot;Password cannot be empty&quot;
    }
  ]
}</pre></td>
    </tr>
        <tr>
      <td>17</td>
      <td><code>org.springframework.web.method.annotation.HandlerMethodValidationException</code><br><sub>extends ResponseStatusException → ErrorResponseException</sub><br><code>GET /mvc-extended-problem-detail/handler-method-validation-exception-path-variable/a</code><br><pre>PathVariable: id=a</pre></td>
      <td><pre>status: 400
Content-Type: application/problem+json

{
  &quot;type&quot;: &quot;about:blank&quot;,
  &quot;title&quot;: &quot;Bad Request&quot;,
  &quot;status&quot;: 400,
  &quot;detail&quot;: &quot;Validation failure&quot;,
  &quot;instance&quot;: &quot;/mvc-extended-problem-detail/handler-method-validation-exception-path-variable/a&quot;,
  &quot;errors&quot;: [
    {
      &quot;type&quot;: &quot;PARAMETER&quot;,
      &quot;target&quot;: &quot;id&quot;,
      &quot;message&quot;: &quot;ID minimum length is 2&quot;
    }
  ]
}</pre></td>
    </tr>
        <tr>
      <td>18</td>
      <td><code>org.springframework.web.method.annotation.HandlerMethodValidationException</code><br><sub>extends ResponseStatusException → ErrorResponseException</sub><br><code>POST /mvc-extended-problem-detail/handler-method-validation-exception-request-body</code><br><pre>Content-Type: application/json; Body: {&quot;name&quot;:&quot;abc&quot;}</pre></td>
      <td><pre>status: 400
Content-Type: application/problem+json

{
  &quot;type&quot;: &quot;about:blank&quot;,
  &quot;title&quot;: &quot;Bad Request&quot;,
  &quot;status&quot;: 400,
  &quot;detail&quot;: &quot;Validation failure&quot;,
  &quot;instance&quot;: &quot;/mvc-extended-problem-detail/handler-method-validation-exception-request-body&quot;,
  &quot;errors&quot;: [
    {
      &quot;type&quot;: &quot;PARAMETER&quot;,
      &quot;target&quot;: &quot;password&quot;,
      &quot;message&quot;: &quot;Password cannot be empty&quot;
    }
  ]
}</pre></td>
    </tr>
        <tr>
      <td>19</td>
      <td><code>org.springframework.web.method.annotation.HandlerMethodValidationException</code><br><sub>extends ResponseStatusException → ErrorResponseException</sub><br><code>POST /mvc-extended-problem-detail/handler-method-validation-exception-request-body-validation-result</code><br><pre>Content-Type: application/json; Body: [&quot;&quot;,&quot;a&quot;]</pre></td>
      <td><pre>status: 400
Content-Type: application/problem+json

{
  &quot;type&quot;: &quot;about:blank&quot;,
  &quot;title&quot;: &quot;Bad Request&quot;,
  &quot;status&quot;: 400,
  &quot;detail&quot;: &quot;Validation failure&quot;,
  &quot;instance&quot;: &quot;/mvc-extended-problem-detail/handler-method-validation-exception-request-body-validation-result&quot;,
  &quot;errors&quot;: [
    {
      &quot;type&quot;: &quot;PARAMETER&quot;,
      &quot;message&quot;: &quot;Element cannot contain empty values&quot;
    }
  ]
}</pre></td>
    </tr>
        <tr>
      <td>20</td>
      <td><code>org.springframework.web.method.annotation.HandlerMethodValidationException</code><br><sub>extends ResponseStatusException → ErrorResponseException</sub><br><code>GET /mvc-extended-problem-detail/handler-method-validation-exception-request-header</code><br><pre>Header: headerValue=a</pre></td>
      <td><pre>status: 400
Content-Type: application/problem+json

{
  &quot;type&quot;: &quot;about:blank&quot;,
  &quot;title&quot;: &quot;Bad Request&quot;,
  &quot;status&quot;: 400,
  &quot;detail&quot;: &quot;Validation failure&quot;,
  &quot;instance&quot;: &quot;/mvc-extended-problem-detail/handler-method-validation-exception-request-header&quot;,
  &quot;errors&quot;: [
    {
      &quot;type&quot;: &quot;HEADER&quot;,
      &quot;target&quot;: &quot;headerValue&quot;,
      &quot;message&quot;: &quot;Minimum length is 2&quot;
    }
  ]
}</pre></td>
    </tr>
        <tr>
      <td>21</td>
      <td><code>org.springframework.web.method.annotation.HandlerMethodValidationException</code><br><sub>extends ResponseStatusException → ErrorResponseException</sub><br><code>GET /mvc-extended-problem-detail/handler-method-validation-exception-request-param</code><br><pre>None (intentionally missing `param` and `param2`)</pre></td>
      <td><pre>status: 400
Content-Type: application/problem+json

{
  &quot;type&quot;: &quot;about:blank&quot;,
  &quot;title&quot;: &quot;Bad Request&quot;,
  &quot;status&quot;: 400,
  &quot;detail&quot;: &quot;Validation failure&quot;,
  &quot;instance&quot;: &quot;/mvc-extended-problem-detail/handler-method-validation-exception-request-param&quot;,
  &quot;errors&quot;: [
    {
      &quot;type&quot;: &quot;PARAMETER&quot;,
      &quot;target&quot;: &quot;param&quot;,
      &quot;message&quot;: &quot;Parameter cannot be empty&quot;
    },
    {
      &quot;type&quot;: &quot;PARAMETER&quot;,
      &quot;target&quot;: &quot;param2&quot;,
      &quot;message&quot;: &quot;Parameter 2 cannot be null&quot;
    },
    {
      &quot;type&quot;: &quot;PARAMETER&quot;,
      &quot;target&quot;: &quot;param2&quot;,
      &quot;message&quot;: &quot;Parameter 2 cannot be blank&quot;
    }
  ]
}</pre></td>
    </tr>
        <tr>
      <td>22</td>
      <td><code>org.springframework.web.method.annotation.HandlerMethodValidationException</code><br><sub>extends ResponseStatusException → ErrorResponseException</sub><br><code>GET /mvc-extended-problem-detail/handler-method-validation-exception-request-part</code><br><pre>Header: Content-Type=multipart/form-data</pre></td>
      <td><pre>status: 400
Content-Type: application/problem+json

{
  &quot;type&quot;: &quot;about:blank&quot;,
  &quot;title&quot;: &quot;Bad Request&quot;,
  &quot;status&quot;: 400,
  &quot;detail&quot;: &quot;Validation failure&quot;,
  &quot;instance&quot;: &quot;/mvc-extended-problem-detail/handler-method-validation-exception-request-part&quot;,
  &quot;errors&quot;: [
    {
      &quot;type&quot;: &quot;PARAMETER&quot;,
      &quot;target&quot;: &quot;file&quot;,
      &quot;message&quot;: &quot;File cannot be empty&quot;
    }
  ]
}</pre></td>
    </tr>
        <tr>
      <td>23</td>
      <td><code>org.springframework.web.method.annotation.HandlerMethodValidationException</code><br><sub>extends ResponseStatusException → ErrorResponseException</sub><br><code>GET /mvc-extended-problem-detail/handler-method-validation-exception-other</code><br><pre>None</pre></td>
      <td><pre>status: 400
Content-Type: application/problem+json

{
  &quot;type&quot;: &quot;about:blank&quot;,
  &quot;title&quot;: &quot;Bad Request&quot;,
  &quot;status&quot;: 400,
  &quot;detail&quot;: &quot;Validation failure&quot;,
  &quot;instance&quot;: &quot;/mvc-extended-problem-detail/handler-method-validation-exception-other&quot;
}</pre></td>
    </tr>
        <tr>
      <td>24</td>
      <td><code>org.springframework.web.servlet.NoHandlerFoundException</code><br><code>GET /mvc-extended-problem-detail/no-handler-found-exception</code><br><pre>None (requires `spring.web.resources.add-mappings=false`)</pre></td>
      <td><pre>status: 404
Content-Type: application/problem+json

{
  &quot;type&quot;: &quot;about:blank&quot;,
  &quot;title&quot;: &quot;Not Found&quot;,
  &quot;status&quot;: 404,
  &quot;detail&quot;: &quot;No static resource mvc-extended-problem-detail/no-handler-found-exception.&quot;,
  &quot;instance&quot;: &quot;/mvc-extended-problem-detail/no-handler-found-exception&quot;
}</pre></td>
    </tr>
        <tr>
      <td>25</td>
      <td><code>org.springframework.web.servlet.resource.NoResourceFoundException</code><br><code>GET /mvc-extended-problem-detail/no-resource-found-exception</code><br><pre>None</pre></td>
      <td><pre>status: 404
Content-Type: application/problem+json

{
  &quot;type&quot;: &quot;about:blank&quot;,
  &quot;title&quot;: &quot;Not Found&quot;,
  &quot;status&quot;: 404,
  &quot;detail&quot;: &quot;No static resource mvc-extended-problem-detail/no-resource-found-exception.&quot;,
  &quot;instance&quot;: &quot;/mvc-extended-problem-detail/no-resource-found-exception&quot;
}</pre></td>
    </tr>
        <tr>
      <td>26</td>
      <td><code>org.springframework.web.context.request.async.AsyncRequestTimeoutException</code><br><code>GET /mvc-extended-problem-detail/async-request-timeout-exception</code><br><pre>None (async timeout is triggered intentionally in the test)</pre></td>
      <td><pre>status: 503
Content-Type: application/problem+json

{
  &quot;type&quot;: &quot;about:blank&quot;,
  &quot;title&quot;: &quot;Service Unavailable&quot;,
  &quot;status&quot;: 503,
  &quot;instance&quot;: &quot;/mvc-extended-problem-detail/async-request-timeout-exception&quot;
}</pre></td>
    </tr>
        <tr>
      <td>27</td>
      <td><code>org.springframework.web.ErrorResponseException</code><br><code>GET /mvc-extended-problem-detail/error-response-exception</code><br><pre>None</pre></td>
      <td><pre>status: 400
Content-Type: application/problem+json

{
  &quot;type&quot;: &quot;about:blank&quot;,
  &quot;title&quot;: &quot;Bad Request&quot;,
  &quot;status&quot;: 400,
  &quot;instance&quot;: &quot;/mvc-extended-problem-detail/error-response-exception&quot;
}</pre></td>
    </tr>
        <tr>
      <td>28</td>
      <td><code>io.github.sbracely.extended.problem.detail.webmvc.example.exception.MvcExtendedErrorResponseException</code><br><sub>extends ErrorResponseException</sub><br><code>GET /mvc-extended-problem-detail/extended-error-response-exception</code><br><pre>None</pre></td>
      <td><pre>status: 500
Content-Type: application/problem+json

{
  &quot;type&quot;: &quot;about:blank&quot;,
  &quot;title&quot;: &quot;Internal Server Error&quot;,
  &quot;status&quot;: 500,
  &quot;detail&quot;: &quot;Payment failed&quot;,
  &quot;instance&quot;: &quot;/mvc-extended-problem-detail/extended-error-response-exception&quot;,
  &quot;errors&quot;: [
    {
      &quot;type&quot;: &quot;BUSINESS&quot;,
      &quot;message&quot;: &quot;Insufficient balance&quot;
    },
    {
      &quot;type&quot;: &quot;BUSINESS&quot;,
      &quot;message&quot;: &quot;Payment frequent&quot;
    }
  ]
}</pre></td>
    </tr>
        <tr>
      <td>29</td>
      <td><code>org.springframework.web.server.MethodNotAllowedException</code><br><sub>extends ResponseStatusException → ErrorResponseException</sub><br><code>DELETE /mvc-extended-problem-detail/method-not-allowed-exception</code><br><pre>None</pre></td>
      <td><pre>status: 405
Content-Type: application/problem+json
Allow: GET, POST

{
  &quot;type&quot;: &quot;about:blank&quot;,
  &quot;title&quot;: &quot;Method Not Allowed&quot;,
  &quot;status&quot;: 405,
  &quot;detail&quot;: &quot;Supported methods: [GET, POST]&quot;,
  &quot;instance&quot;: &quot;/mvc-extended-problem-detail/method-not-allowed-exception&quot;
}</pre></td>
    </tr>
        <tr>
      <td>30</td>
      <td><code>org.springframework.web.server.NotAcceptableStatusException</code><br><sub>extends ResponseStatusException → ErrorResponseException</sub><br><code>GET /mvc-extended-problem-detail/not-acceptable-status-exception</code><br><pre>None</pre></td>
      <td><pre>status: 406
Content-Type: application/problem+json
Accept: application/json

{
  &quot;type&quot;: &quot;about:blank&quot;,
  &quot;title&quot;: &quot;Not Acceptable&quot;,
  &quot;status&quot;: 406,
  &quot;detail&quot;: &quot;Acceptable representations: [application/json].&quot;,
  &quot;instance&quot;: &quot;/mvc-extended-problem-detail/not-acceptable-status-exception&quot;
}</pre></td>
    </tr>
        <tr>
      <td>31</td>
      <td><code>org.springframework.web.server.UnsupportedMediaTypeStatusException</code><br><sub>extends ResponseStatusException → ErrorResponseException</sub><br><code>POST /mvc-extended-problem-detail/unsupported-media-type-status-exception</code><br><pre>None (no valid Content-Type sent)</pre></td>
      <td><pre>status: 415
Content-Type: application/problem+json

{
  &quot;type&quot;: &quot;about:blank&quot;,
  &quot;title&quot;: &quot;Unsupported Media Type&quot;,
  &quot;status&quot;: 415,
  &quot;detail&quot;: &quot;Could not parse Content-Type.&quot;,
  &quot;instance&quot;: &quot;/mvc-extended-problem-detail/unsupported-media-type-status-exception&quot;
}</pre></td>
    </tr>
        <tr>
      <td>32</td>
      <td><code>org.springframework.web.server.ResponseStatusException</code><br><sub>extends ErrorResponseException</sub><br><code>GET /mvc-extended-problem-detail/response-status-exception</code><br><pre>None</pre></td>
      <td><pre>status: 400
Content-Type: application/problem+json

{
  &quot;type&quot;: &quot;about:blank&quot;,
  &quot;title&quot;: &quot;Bad Request&quot;,
  &quot;status&quot;: 400,
  &quot;detail&quot;: &quot;exception&quot;,
  &quot;instance&quot;: &quot;/mvc-extended-problem-detail/response-status-exception&quot;
}</pre></td>
    </tr>
        <tr>
      <td>33</td>
      <td><code>InvalidEndpointBadRequestException</code><br><sub>extends ResponseStatusException</sub><br><code>GET /actuator/demo/name</code><br><pre>None (requires `management.endpoints.web.exposure.include=demo`)</pre></td>
      <td><pre>status: 400
Content-Type: application/problem+json

{
  &quot;type&quot;: &quot;about:blank&quot;,
  &quot;title&quot;: &quot;Bad Request&quot;,
  &quot;status&quot;: 400,
  &quot;detail&quot;: &quot;Missing parameters: param1,param2&quot;,
  &quot;instance&quot;: &quot;/actuator/demo/name&quot;
}</pre></td>
    </tr>
        <tr>
      <td>34</td>
      <td><code>org.springframework.web.server.ServerWebInputException</code><br><sub>extends ResponseStatusException → ErrorResponseException</sub><br><code>GET /mvc-extended-problem-detail/server-web-input-exception</code><br><pre>None</pre></td>
      <td><pre>status: 400
Content-Type: application/problem+json

{
  &quot;type&quot;: &quot;about:blank&quot;,
  &quot;title&quot;: &quot;Bad Request&quot;,
  &quot;status&quot;: 400,
  &quot;detail&quot;: &quot;server web input error&quot;,
  &quot;instance&quot;: &quot;/mvc-extended-problem-detail/server-web-input-exception&quot;
}</pre></td>
    </tr>
        <tr>
      <td>35</td>
      <td><code>org.springframework.web.server.MissingRequestValueException</code><br><sub>extends ServerWebInputException → ResponseStatusException</sub><br><code>GET /mvc-extended-problem-detail/org-springframework-web-server-missing-request-value-exception</code><br><pre>None (missing request parameter `id`)</pre></td>
      <td><pre>status: 400
Content-Type: application/problem+json

{
  &quot;type&quot;: &quot;about:blank&quot;,
  &quot;title&quot;: &quot;Bad Request&quot;,
  &quot;status&quot;: 400,
  &quot;detail&quot;: &quot;Required request param &#39;id&#39; is not present.&quot;,
  &quot;instance&quot;: &quot;/mvc-extended-problem-detail/org-springframework-web-server-missing-request-value-exception&quot;
}</pre></td>
    </tr>
        <tr>
      <td>36</td>
      <td><code>org.springframework.web.server.UnsatisfiedRequestParameterException</code><br><sub>extends ServerWebInputException → ResponseStatusException</sub><br><code>GET /mvc-extended-problem-detail/unsatisfied-request-parameter-exception</code><br><pre>Query: type=1 (does not satisfy exist and !debug)</pre></td>
      <td><pre>status: 400
Content-Type: application/problem+json

{
  &quot;type&quot;: &quot;about:blank&quot;,
  &quot;title&quot;: &quot;Bad Request&quot;,
  &quot;status&quot;: 400,
  &quot;detail&quot;: &quot;Invalid request parameters.&quot;,
  &quot;instance&quot;: &quot;/mvc-extended-problem-detail/unsatisfied-request-parameter-exception&quot;
}</pre></td>
    </tr>
        <tr>
      <td>37</td>
      <td><code>org.springframework.web.bind.support.WebExchangeBindException</code><br><sub>extends ServerWebInputException → ResponseStatusException</sub><br><code>POST /mvc-extended-problem-detail/web-exchange-bind-exception</code><br><pre>Content-Type: application/json; Body: {&quot;name&quot;:&quot;abc&quot;,&quot;password&quot;:&quot;123&quot;}</pre></td>
      <td><pre>status: 400
Content-Type: application/problem+json

{
  &quot;type&quot;: &quot;about:blank&quot;,
  &quot;title&quot;: &quot;Bad Request&quot;,
  &quot;status&quot;: 400,
  &quot;detail&quot;: &quot;Invalid request content.&quot;,
  &quot;instance&quot;: &quot;/mvc-extended-problem-detail/web-exchange-bind-exception&quot;,
  &quot;errors&quot;: [
    {
      &quot;type&quot;: &quot;PARAMETER&quot;,
      &quot;target&quot;: &quot;name&quot;,
      &quot;message&quot;: &quot;Name length must be between 6-10&quot;
    },
    {
      &quot;type&quot;: &quot;PARAMETER&quot;,
      &quot;target&quot;: &quot;age&quot;,
      &quot;message&quot;: &quot;Age cannot be null&quot;
    },
    {
      &quot;type&quot;: &quot;PARAMETER&quot;,
      &quot;target&quot;: &quot;password&quot;,
      &quot;message&quot;: &quot;Password and confirm password do not match&quot;
    },
    {
      &quot;type&quot;: &quot;PARAMETER&quot;,
      &quot;target&quot;: &quot;confirmPassword&quot;,
      &quot;message&quot;: &quot;Password and confirm password do not match&quot;
    }
  ]
}</pre></td>
    </tr>
        <tr>
      <td>38</td>
      <td><code>org.springframework.web.server.ServerErrorException</code><br><sub>extends ResponseStatusException → ErrorResponseException</sub><br><code>GET /mvc-extended-problem-detail/server-error-exception</code><br><pre>None</pre></td>
      <td><pre>status: 500
Content-Type: application/problem+json

{
  &quot;type&quot;: &quot;about:blank&quot;,
  &quot;title&quot;: &quot;Internal Server Error&quot;,
  &quot;status&quot;: 500,
  &quot;detail&quot;: &quot;server error&quot;,
  &quot;instance&quot;: &quot;/mvc-extended-problem-detail/server-error-exception&quot;
}</pre></td>
    </tr>
        <tr>
      <td>39</td>
      <td><code>org.springframework.web.server.PayloadTooLargeException</code><br><sub>extends ResponseStatusException → ErrorResponseException</sub><br><code>POST /mvc-extended-problem-detail/payload-too-large-exception</code><br><pre>multipart/form-data; upload `file=test.txt`</pre></td>
      <td><pre>status: 413
Content-Type: application/problem+json

{
  &quot;type&quot;: &quot;about:blank&quot;,
  &quot;title&quot;: &quot;Payload Too Large&quot;,
  &quot;status&quot;: 413,
  &quot;detail&quot;: &quot;payload too large&quot;,
  &quot;instance&quot;: &quot;/mvc-extended-problem-detail/payload-too-large-exception&quot;
}</pre></td>
    </tr>
        <tr>
      <td>40</td>
      <td><code>org.springframework.web.multipart.MaxUploadSizeExceededException</code><br><code>POST /mvc-extended-problem-detail/max-upload-size-exceeded-exception</code><br><pre>multipart/form-data; upload a 2-byte file; requires `spring.servlet.multipart.max-file-size=1`</pre></td>
      <td><pre>status: 413
Content-Type: application/problem+json

{
  &quot;type&quot;: &quot;about:blank&quot;,
  &quot;title&quot;: &quot;Payload Too Large&quot;,
  &quot;status&quot;: 413,
  &quot;detail&quot;: &quot;Maximum upload size exceeded&quot;,
  &quot;instance&quot;: &quot;/mvc-extended-problem-detail/max-upload-size-exceeded-exception&quot;
}</pre></td>
    </tr>
        <tr>
      <td>41</td>
      <td><code>org.springframework.beans.ConversionNotSupportedException</code><br><code>GET /mvc-extended-problem-detail/conversion-not-supported-exception</code><br><pre>Query: data=test-value</pre></td>
      <td><pre>status: 500
Content-Type: application/problem+json

{
  &quot;type&quot;: &quot;about:blank&quot;,
  &quot;title&quot;: &quot;Internal Server Error&quot;,
  &quot;status&quot;: 500,
  &quot;detail&quot;: &quot;Failed to convert &#39;null&#39; with value: &#39;test-value&#39;&quot;,
  &quot;instance&quot;: &quot;/mvc-extended-problem-detail/conversion-not-supported-exception&quot;
}</pre></td>
    </tr>
        <tr>
      <td>42</td>
      <td><code>org.springframework.web.method.annotation.MethodArgumentConversionNotSupportedException</code><br><sub>extends ConversionNotSupportedException → TypeMismatchException</sub><br><code>GET /mvc-extended-problem-detail/method-argument-conversion-not-supported-exception</code><br><pre>Query: error=test-value</pre></td>
      <td><pre>status: 500
Content-Type: application/problem+json

{
  &quot;type&quot;: &quot;about:blank&quot;,
  &quot;title&quot;: &quot;Internal Server Error&quot;,
  &quot;status&quot;: 500,
  &quot;detail&quot;: &quot;Failed to convert &#39;error&#39; with value: &#39;test-value&#39;&quot;,
  &quot;instance&quot;: &quot;/mvc-extended-problem-detail/method-argument-conversion-not-supported-exception&quot;
}</pre></td>
    </tr>
        <tr>
      <td>43</td>
      <td><code>org.springframework.beans.TypeMismatchException</code><br><code>GET /mvc-extended-problem-detail/type-mismatch-exception</code><br><pre>None</pre></td>
      <td><pre>status: 400
Content-Type: application/problem+json

{
  &quot;type&quot;: &quot;about:blank&quot;,
  &quot;title&quot;: &quot;Bad Request&quot;,
  &quot;status&quot;: 400,
  &quot;detail&quot;: &quot;Failed to convert &#39;null&#39; with value: &#39;test&#39;&quot;,
  &quot;instance&quot;: &quot;/mvc-extended-problem-detail/type-mismatch-exception&quot;
}</pre></td>
    </tr>
        <tr>
      <td>44</td>
      <td><code>org.springframework.web.method.annotation.MethodArgumentTypeMismatchException</code><br><sub>extends TypeMismatchException</sub><br><code>GET /mvc-extended-problem-detail/method-argument-type-mismatch-exception</code><br><pre>Query: integer=a</pre></td>
      <td><pre>status: 400
Content-Type: application/problem+json

{
  &quot;type&quot;: &quot;about:blank&quot;,
  &quot;title&quot;: &quot;Bad Request&quot;,
  &quot;status&quot;: 400,
  &quot;detail&quot;: &quot;Failed to convert &#39;integer&#39; with value: &#39;a&#39;&quot;,
  &quot;instance&quot;: &quot;/mvc-extended-problem-detail/method-argument-type-mismatch-exception&quot;
}</pre></td>
    </tr>
        <tr>
      <td>45</td>
      <td><code>org.springframework.http.converter.HttpMessageNotReadableException</code><br><code>POST /mvc-extended-problem-detail/http-message-not-readable-exception</code><br><pre>Content-Type: application/json; Body: {</pre></td>
      <td><pre>status: 400
Content-Type: application/problem+json

{
  &quot;type&quot;: &quot;about:blank&quot;,
  &quot;title&quot;: &quot;Bad Request&quot;,
  &quot;status&quot;: 400,
  &quot;detail&quot;: &quot;Failed to read request&quot;,
  &quot;instance&quot;: &quot;/mvc-extended-problem-detail/http-message-not-readable-exception&quot;
}</pre></td>
    </tr>
        <tr>
      <td>46</td>
      <td><code>org.springframework.http.converter.HttpMessageNotWritableException</code><br><code>GET /mvc-extended-problem-detail/http-message-not-writable-exception</code><br><pre>None</pre></td>
      <td><pre>status: 500
Content-Type: application/json

{
  &quot;type&quot;: &quot;about:blank&quot;,
  &quot;title&quot;: &quot;Internal Server Error&quot;,
  &quot;status&quot;: 500,
  &quot;detail&quot;: &quot;Failed to write request&quot;,
  &quot;instance&quot;: &quot;/mvc-extended-problem-detail/http-message-not-writable-exception&quot;
}</pre></td>
    </tr>
        <tr>
      <td>47</td>
      <td><code>org.springframework.validation.method.MethodValidationException</code><br><code>GET /mvc-extended-problem-detail/method-validation-exception</code><br><pre>None</pre></td>
      <td><pre>status: 500
Content-Type: application/problem+json

{
  &quot;type&quot;: &quot;about:blank&quot;,
  &quot;title&quot;: &quot;Internal Server Error&quot;,
  &quot;status&quot;: 500,
  &quot;detail&quot;: &quot;Validation failed&quot;,
  &quot;instance&quot;: &quot;/mvc-extended-problem-detail/method-validation-exception&quot;,
  &quot;errors&quot;: [
    {
      &quot;type&quot;: &quot;PARAMETER&quot;,
      &quot;target&quot;: &quot;name&quot;,
      &quot;message&quot;: &quot;name must not be blank&quot;
    },
    {
      &quot;type&quot;: &quot;PARAMETER&quot;,
      &quot;target&quot;: &quot;name&quot;,
      &quot;message&quot;: &quot;name must not be null&quot;
    },
    {
      &quot;type&quot;: &quot;PARAMETER&quot;,
      &quot;target&quot;: &quot;password&quot;,
      &quot;message&quot;: &quot;Password and confirm password do not match&quot;
    },
    {
      &quot;type&quot;: &quot;PARAMETER&quot;,
      &quot;target&quot;: &quot;name&quot;,
      &quot;message&quot;: &quot;Name cannot be blank&quot;
    },
    {
      &quot;type&quot;: &quot;PARAMETER&quot;,
      &quot;target&quot;: &quot;age&quot;,
      &quot;message&quot;: &quot;Age cannot be null&quot;
    },
    {
      &quot;type&quot;: &quot;PARAMETER&quot;,
      &quot;target&quot;: &quot;confirmPassword&quot;,
      &quot;message&quot;: &quot;Password and confirm password do not match&quot;
    },
    {
      &quot;type&quot;: &quot;PARAMETER&quot;,
      &quot;target&quot;: &quot;name&quot;,
      &quot;message&quot;: &quot;Name length must be between 6-10&quot;
    },
    {
      &quot;type&quot;: &quot;PARAMETER&quot;,
      &quot;message&quot;: &quot;Name is not valid&quot;
    }
  ]
}</pre></td>
    </tr>
        <tr>
      <td>48</td>
      <td><code>org.springframework.web.context.request.async.AsyncRequestNotUsableException</code><br><code>GET /mvc-extended-problem-detail/async-request-not-usable-exception</code><br><pre>Header: Accept=text/event-stream</pre></td>
      <td><pre>status: 200
Content-Type: text/event-stream

data:event 0

data:event 1

data:event 2

... (the stream normally keeps sending data; AsyncRequestNotUsableException is only triggered after the client times out/disconnects and the server writes again)</pre></td>
    </tr>
  </tbody>
</table>



