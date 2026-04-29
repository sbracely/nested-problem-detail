# WebFlux Example Application Exception Response Table

- Data source: real responses captured by starting the current WebFlux example application and sending real requests.
- Ordering: follows the current `ResponseEntityExceptionHandler#handleException` dispatch order; subclasses are placed next to their parent class.
- The 3 API-version rows are triggered with the API-version configuration: `spring.webflux.apiversion.use.header=API-Version`, `spring.webflux.apiversion.supported=1,2`; `/not-acceptable-api-version` is registered by `FluxApiVersionController` only when that configuration is enabled.

<table>
  <thead>
    <tr><th>No.</th><th>Scenario</th><th>Response</th></tr>
  </thead>
  <tbody>
        <tr>
      <td>1</td>
      <td><code>org.springframework.web.server.MethodNotAllowedException</code><br><sub>extends ResponseStatusException → ErrorResponseException</sub><br><code>DELETE /flux-extended-problem-detail/method-not-allowed-exception</code><br><pre>None</pre></td>
      <td><pre>status: 405
Content-Type: application/problem+json
Allow: GET

{
&nbsp;&nbsp;&quot;detail&quot; : &quot;Supported methods: [GET]&quot;,
&nbsp;&nbsp;&quot;instance&quot; : &quot;/flux-extended-problem-detail/method-not-allowed-exception&quot;,
&nbsp;&nbsp;&quot;status&quot; : 405,
&nbsp;&nbsp;&quot;title&quot; : &quot;Method Not Allowed&quot;
}</pre></td>
    </tr>
        <tr>
      <td>2</td>
      <td><code>org.springframework.web.server.NotAcceptableStatusException</code><br><sub>extends ResponseStatusException → ErrorResponseException</sub><br><code>GET /flux-extended-problem-detail/not-acceptable-status-exception</code><br><pre>Header: Accept=application/xml</pre></td>
      <td><pre>status: 406
Content-Type: application/problem+json
Accept: application/json

{
&nbsp;&nbsp;&quot;detail&quot; : &quot;Acceptable representations: [application/json].&quot;,
&nbsp;&nbsp;&quot;instance&quot; : &quot;/flux-extended-problem-detail/not-acceptable-status-exception&quot;,
&nbsp;&nbsp;&quot;status&quot; : 406,
&nbsp;&nbsp;&quot;title&quot; : &quot;Not Acceptable&quot;
}</pre></td>
    </tr>
        <tr>
      <td>3</td>
      <td><code>org.springframework.web.server.UnsupportedMediaTypeStatusException</code><br><sub>extends ResponseStatusException → ErrorResponseException</sub><br><code>POST /flux-extended-problem-detail/unsupported-media-type-status-exception</code><br><pre>None (Content-Type: application/xml not sent)</pre></td>
      <td><pre>status: 415
Content-Type: application/problem+json
Accept: application/xml

{
&nbsp;&nbsp;&quot;instance&quot; : &quot;/flux-extended-problem-detail/unsupported-media-type-status-exception&quot;,
&nbsp;&nbsp;&quot;status&quot; : 415,
&nbsp;&nbsp;&quot;title&quot; : &quot;Unsupported Media Type&quot;
}</pre></td>
    </tr>
        <tr>
      <td>4</td>
      <td><code>org.springframework.web.server.MissingRequestValueException</code><br><sub>extends ServerWebInputException → ResponseStatusException</sub><br><code>GET /flux-extended-problem-detail/missing-request-value-exception</code><br><pre>None (missing query parameter id)</pre></td>
      <td><pre>status: 400
Content-Type: application/problem+json

{
&nbsp;&nbsp;&quot;detail&quot; : &quot;Required query parameter &#x27;id&#x27; is not present.&quot;,
&nbsp;&nbsp;&quot;instance&quot; : &quot;/flux-extended-problem-detail/missing-request-value-exception&quot;,
&nbsp;&nbsp;&quot;status&quot; : 400,
&nbsp;&nbsp;&quot;title&quot; : &quot;Bad Request&quot;
}</pre></td>
    </tr>
        <tr>
      <td>5</td>
      <td><code>org.springframework.web.server.UnsatisfiedRequestParameterException</code><br><sub>extends ServerWebInputException → ResponseStatusException</sub><br><code>GET /flux-extended-problem-detail/unsatisfied-request-parameter-exception</code><br><pre>None (does not satisfy params condition `type=1`, `exist`, `!debug`)</pre></td>
      <td><pre>status: 400
Content-Type: application/problem+json

{
&nbsp;&nbsp;&quot;detail&quot; : &quot;Invalid request parameters.&quot;,
&nbsp;&nbsp;&quot;instance&quot; : &quot;/flux-extended-problem-detail/unsatisfied-request-parameter-exception&quot;,
&nbsp;&nbsp;&quot;status&quot; : 400,
&nbsp;&nbsp;&quot;title&quot; : &quot;Bad Request&quot;
}</pre></td>
    </tr>
        <tr>
      <td>6</td>
      <td><code>org.springframework.web.bind.support.WebExchangeBindException</code><br><sub>extends ServerWebInputException → ResponseStatusException</sub><br><code>POST /flux-extended-problem-detail/web-exchange-bind-exception</code><br><pre>Content-Type: application/json；Body: {&quot;name&quot;:&quot;abc&quot;,&quot;password&quot;:&quot;123&quot;}</pre></td>
      <td><pre>status: 400
Content-Type: application/problem+json

{
&nbsp;&nbsp;&quot;detail&quot; : &quot;Invalid request content.&quot;,
&nbsp;&nbsp;&quot;errors&quot; : [
&nbsp;&nbsp;&nbsp;&nbsp;{
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&quot;type&quot; : &quot;PARAMETER&quot;,
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&quot;target&quot; : &quot;name&quot;,
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&quot;message&quot; : &quot;Name length must be between 6-10&quot;
&nbsp;&nbsp;&nbsp;&nbsp;},
&nbsp;&nbsp;&nbsp;&nbsp;{
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&quot;type&quot; : &quot;PARAMETER&quot;,
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&quot;target&quot; : &quot;confirmPassword&quot;,
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&quot;message&quot; : &quot;Password and confirm password do not match&quot;
&nbsp;&nbsp;&nbsp;&nbsp;},
&nbsp;&nbsp;&nbsp;&nbsp;{
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&quot;type&quot; : &quot;PARAMETER&quot;,
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&quot;target&quot; : &quot;password&quot;,
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&quot;message&quot; : &quot;Password and confirm password do not match&quot;
&nbsp;&nbsp;&nbsp;&nbsp;},
&nbsp;&nbsp;&nbsp;&nbsp;{
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&quot;type&quot; : &quot;PARAMETER&quot;,
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&quot;target&quot; : &quot;age&quot;,
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&quot;message&quot; : &quot;Age cannot be null&quot;
&nbsp;&nbsp;&nbsp;&nbsp;}
&nbsp;&nbsp;],
&nbsp;&nbsp;&quot;instance&quot; : &quot;/flux-extended-problem-detail/web-exchange-bind-exception&quot;,
&nbsp;&nbsp;&quot;status&quot; : 400,
&nbsp;&nbsp;&quot;title&quot; : &quot;Bad Request&quot;
}</pre></td>
    </tr>
        <tr>
      <td>7</td>
      <td><code>org.springframework.web.method.annotation.HandlerMethodValidationException</code><br><sub>extends ResponseStatusException → ErrorResponseException</sub><br><code>GET /flux-extended-problem-detail/handler-method-validation-exception-cookie-value</code><br><pre>Cookie: cookieValue=</pre></td>
      <td><pre>status: 400
Content-Type: application/problem+json

{
&nbsp;&nbsp;&quot;detail&quot; : &quot;Validation failure&quot;,
&nbsp;&nbsp;&quot;errors&quot; : [
&nbsp;&nbsp;&nbsp;&nbsp;{
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&quot;type&quot; : &quot;COOKIE&quot;,
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&quot;target&quot; : &quot;cookieValue&quot;,
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&quot;message&quot; : &quot;cookie cannot be empty&quot;
&nbsp;&nbsp;&nbsp;&nbsp;}
&nbsp;&nbsp;],
&nbsp;&nbsp;&quot;instance&quot; : &quot;/flux-extended-problem-detail/handler-method-validation-exception-cookie-value&quot;,
&nbsp;&nbsp;&quot;status&quot; : 400,
&nbsp;&nbsp;&quot;title&quot; : &quot;Bad Request&quot;
}</pre></td>
    </tr>
        <tr>
      <td>8</td>
      <td><code>org.springframework.web.method.annotation.HandlerMethodValidationException</code><br><sub>extends ResponseStatusException → ErrorResponseException</sub><br><code>GET /flux-extended-problem-detail/handler-method-validation-exception-matrix/abc;list=a,b,c</code><br><pre>Path matrix: list=a,b,c</pre></td>
      <td><pre>status: 400
Content-Type: application/problem+json

{
&nbsp;&nbsp;&quot;detail&quot; : &quot;Validation failure&quot;,
&nbsp;&nbsp;&quot;errors&quot; : [
&nbsp;&nbsp;&nbsp;&nbsp;{
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&quot;type&quot; : &quot;PARAMETER&quot;,
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&quot;target&quot; : &quot;list&quot;,
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&quot;message&quot; : &quot;list maximum size is 2&quot;
&nbsp;&nbsp;&nbsp;&nbsp;}
&nbsp;&nbsp;],
&nbsp;&nbsp;&quot;instance&quot; : &quot;/flux-extended-problem-detail/handler-method-validation-exception-matrix/abc;list=a,b,c&quot;,
&nbsp;&nbsp;&quot;status&quot; : 400,
&nbsp;&nbsp;&quot;title&quot; : &quot;Bad Request&quot;
}</pre></td>
    </tr>
        <tr>
      <td>9</td>
      <td><code>org.springframework.web.method.annotation.HandlerMethodValidationException</code><br><sub>extends ResponseStatusException → ErrorResponseException</sub><br><code>GET /flux-extended-problem-detail/handler-method-validation-exception-model-attribute</code><br><pre>None</pre></td>
      <td><pre>status: 400
Content-Type: application/problem+json

{
&nbsp;&nbsp;&quot;detail&quot; : &quot;Validation failure&quot;,
&nbsp;&nbsp;&quot;errors&quot; : [
&nbsp;&nbsp;&nbsp;&nbsp;{
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&quot;type&quot; : &quot;PARAMETER&quot;,
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&quot;target&quot; : &quot;password&quot;,
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&quot;message&quot; : &quot;Password cannot be empty&quot;
&nbsp;&nbsp;&nbsp;&nbsp;}
&nbsp;&nbsp;],
&nbsp;&nbsp;&quot;instance&quot; : &quot;/flux-extended-problem-detail/handler-method-validation-exception-model-attribute&quot;,
&nbsp;&nbsp;&quot;status&quot; : 400,
&nbsp;&nbsp;&quot;title&quot; : &quot;Bad Request&quot;
}</pre></td>
    </tr>
        <tr>
      <td>10</td>
      <td><code>org.springframework.web.method.annotation.HandlerMethodValidationException</code><br><sub>extends ResponseStatusException → ErrorResponseException</sub><br><code>GET /flux-extended-problem-detail/handler-method-validation-exception-path-variable/abc</code><br><pre>PathVariable: id=abc</pre></td>
      <td><pre>status: 400
Content-Type: application/problem+json

{
&nbsp;&nbsp;&quot;detail&quot; : &quot;Validation failure&quot;,
&nbsp;&nbsp;&quot;errors&quot; : [
&nbsp;&nbsp;&nbsp;&nbsp;{
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&quot;type&quot; : &quot;PARAMETER&quot;,
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&quot;target&quot; : &quot;id&quot;,
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&quot;message&quot; : &quot;id length must be at least 5&quot;
&nbsp;&nbsp;&nbsp;&nbsp;}
&nbsp;&nbsp;],
&nbsp;&nbsp;&quot;instance&quot; : &quot;/flux-extended-problem-detail/handler-method-validation-exception-path-variable/abc&quot;,
&nbsp;&nbsp;&quot;status&quot; : 400,
&nbsp;&nbsp;&quot;title&quot; : &quot;Bad Request&quot;
}</pre></td>
    </tr>
        <tr>
      <td>11</td>
      <td><code>org.springframework.web.method.annotation.HandlerMethodValidationException</code><br><sub>extends ResponseStatusException → ErrorResponseException</sub><br><code>POST /flux-extended-problem-detail/handler-method-validation-exception-request-body</code><br><pre>Content-Type: application/json；Body: {&quot;name&quot;:&quot;abc&quot;}</pre></td>
      <td><pre>status: 400
Content-Type: application/problem+json

{
&nbsp;&nbsp;&quot;detail&quot; : &quot;Validation failure&quot;,
&nbsp;&nbsp;&quot;errors&quot; : [
&nbsp;&nbsp;&nbsp;&nbsp;{
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&quot;type&quot; : &quot;PARAMETER&quot;,
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&quot;target&quot; : &quot;password&quot;,
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&quot;message&quot; : &quot;Password cannot be empty&quot;
&nbsp;&nbsp;&nbsp;&nbsp;}
&nbsp;&nbsp;],
&nbsp;&nbsp;&quot;instance&quot; : &quot;/flux-extended-problem-detail/handler-method-validation-exception-request-body&quot;,
&nbsp;&nbsp;&quot;status&quot; : 400,
&nbsp;&nbsp;&quot;title&quot; : &quot;Bad Request&quot;
}</pre></td>
    </tr>
        <tr>
      <td>12</td>
      <td><code>org.springframework.web.method.annotation.HandlerMethodValidationException</code><br><sub>extends ResponseStatusException → ErrorResponseException</sub><br><code>POST /flux-extended-problem-detail/handler-method-validation-exception-request-body-validation-result</code><br><pre>Content-Type: application/json；Body: [&quot;&quot;,&quot;a&quot;]</pre></td>
      <td><pre>status: 400
Content-Type: application/problem+json

{
&nbsp;&nbsp;&quot;detail&quot; : &quot;Validation failure&quot;,
&nbsp;&nbsp;&quot;errors&quot; : [
&nbsp;&nbsp;&nbsp;&nbsp;{
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&quot;type&quot; : &quot;PARAMETER&quot;,
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&quot;target&quot; : null,
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&quot;message&quot; : &quot;Element cannot contain empty values&quot;
&nbsp;&nbsp;&nbsp;&nbsp;}
&nbsp;&nbsp;],
&nbsp;&nbsp;&quot;instance&quot; : &quot;/flux-extended-problem-detail/handler-method-validation-exception-request-body-validation-result&quot;,
&nbsp;&nbsp;&quot;status&quot; : 400,
&nbsp;&nbsp;&quot;title&quot; : &quot;Bad Request&quot;
}</pre></td>
    </tr>
        <tr>
      <td>13</td>
      <td><code>org.springframework.web.method.annotation.HandlerMethodValidationException</code><br><sub>extends ResponseStatusException → ErrorResponseException</sub><br><code>GET /flux-extended-problem-detail/handler-method-validation-exception-request-header</code><br><pre>Header: headerValue=</pre></td>
      <td><pre>status: 400
Content-Type: application/problem+json

{
&nbsp;&nbsp;&quot;detail&quot; : &quot;Validation failure&quot;,
&nbsp;&nbsp;&quot;errors&quot; : [
&nbsp;&nbsp;&nbsp;&nbsp;{
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&quot;type&quot; : &quot;HEADER&quot;,
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&quot;target&quot; : &quot;headerValue&quot;,
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&quot;message&quot; : &quot;Header cannot be empty&quot;
&nbsp;&nbsp;&nbsp;&nbsp;}
&nbsp;&nbsp;],
&nbsp;&nbsp;&quot;instance&quot; : &quot;/flux-extended-problem-detail/handler-method-validation-exception-request-header&quot;,
&nbsp;&nbsp;&quot;status&quot; : 400,
&nbsp;&nbsp;&quot;title&quot; : &quot;Bad Request&quot;
}</pre></td>
    </tr>
        <tr>
      <td>14</td>
      <td><code>org.springframework.web.method.annotation.HandlerMethodValidationException</code><br><sub>extends ResponseStatusException → ErrorResponseException</sub><br><code>GET /flux-extended-problem-detail/handler-method-validation-exception-request-param?param=&amp;value=ab</code><br><pre>Query: param=；value=ab</pre></td>
      <td><pre>status: 400
Content-Type: application/problem+json

{
&nbsp;&nbsp;&quot;detail&quot; : &quot;Validation failure&quot;,
&nbsp;&nbsp;&quot;errors&quot; : [
&nbsp;&nbsp;&nbsp;&nbsp;{
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&quot;type&quot; : &quot;PARAMETER&quot;,
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&quot;target&quot; : &quot;param&quot;,
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&quot;message&quot; : &quot;Parameter cannot be empty&quot;
&nbsp;&nbsp;&nbsp;&nbsp;},
&nbsp;&nbsp;&nbsp;&nbsp;{
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&quot;type&quot; : &quot;PARAMETER&quot;,
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&quot;target&quot; : &quot;value&quot;,
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&quot;message&quot; : &quot;Length must be at least 5&quot;
&nbsp;&nbsp;&nbsp;&nbsp;}
&nbsp;&nbsp;],
&nbsp;&nbsp;&quot;instance&quot; : &quot;/flux-extended-problem-detail/handler-method-validation-exception-request-param&quot;,
&nbsp;&nbsp;&quot;status&quot; : 400,
&nbsp;&nbsp;&quot;title&quot; : &quot;Bad Request&quot;
}</pre></td>
    </tr>
        <tr>
      <td>15</td>
      <td><code>org.springframework.web.method.annotation.HandlerMethodValidationException</code><br><sub>extends ResponseStatusException → ErrorResponseException</sub><br><code>POST /flux-extended-problem-detail/handler-method-validation-exception-request-part</code><br><pre>Body: {}</pre></td>
      <td><pre>status: 400
Content-Type: application/problem+json

{
&nbsp;&nbsp;&quot;detail&quot; : &quot;Validation failure&quot;,
&nbsp;&nbsp;&quot;errors&quot; : [
&nbsp;&nbsp;&nbsp;&nbsp;{
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&quot;type&quot; : &quot;PARAMETER&quot;,
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&quot;target&quot; : &quot;file&quot;,
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&quot;message&quot; : &quot;File cannot be empty&quot;
&nbsp;&nbsp;&nbsp;&nbsp;}
&nbsp;&nbsp;],
&nbsp;&nbsp;&quot;instance&quot; : &quot;/flux-extended-problem-detail/handler-method-validation-exception-request-part&quot;,
&nbsp;&nbsp;&quot;status&quot; : 400,
&nbsp;&nbsp;&quot;title&quot; : &quot;Bad Request&quot;
}</pre></td>
    </tr>
        <tr>
      <td>16</td>
      <td><code>org.springframework.web.method.annotation.HandlerMethodValidationException</code><br><sub>extends ResponseStatusException → ErrorResponseException</sub><br><code>GET /flux-extended-problem-detail/handler-method-validation-exception-other</code><br><pre>None</pre></td>
      <td><pre>status: 400
Content-Type: application/problem+json

{
&nbsp;&nbsp;&quot;detail&quot; : &quot;Validation failure&quot;,
&nbsp;&nbsp;&quot;instance&quot; : &quot;/flux-extended-problem-detail/handler-method-validation-exception-other&quot;,
&nbsp;&nbsp;&quot;status&quot; : 400,
&nbsp;&nbsp;&quot;title&quot; : &quot;Bad Request&quot;
}</pre></td>
    </tr>
        <tr>
      <td>17</td>
      <td><code>org.springframework.web.server.ServerWebInputException</code><br><sub>extends ResponseStatusException → ErrorResponseException</sub><br><code>GET /flux-extended-problem-detail/server-web-input-exception</code><br><pre>None</pre></td>
      <td><pre>status: 400
Content-Type: application/problem+json

{
&nbsp;&nbsp;&quot;detail&quot; : &quot;server web input error&quot;,
&nbsp;&nbsp;&quot;instance&quot; : &quot;/flux-extended-problem-detail/server-web-input-exception&quot;,
&nbsp;&nbsp;&quot;status&quot; : 400,
&nbsp;&nbsp;&quot;title&quot; : &quot;Bad Request&quot;
}</pre></td>
    </tr>
        <tr>
      <td>18</td>
      <td><code>org.springframework.web.server.ServerErrorException</code><br><sub>extends ResponseStatusException → ErrorResponseException</sub><br><code>GET /flux-extended-problem-detail/server-error-exception</code><br><pre>None</pre></td>
      <td><pre>status: 500
Content-Type: application/problem+json

{
&nbsp;&nbsp;&quot;detail&quot; : &quot;server error&quot;,
&nbsp;&nbsp;&quot;instance&quot; : &quot;/flux-extended-problem-detail/server-error-exception&quot;,
&nbsp;&nbsp;&quot;status&quot; : 500,
&nbsp;&nbsp;&quot;title&quot; : &quot;Internal Server Error&quot;
}</pre></td>
    </tr>
        <tr>
      <td>19</td>
      <td><code>org.springframework.web.server.ResponseStatusException</code><br><sub>extends ErrorResponseException</sub><br><code>GET /flux-extended-problem-detail/response-status-exception</code><br><pre>None</pre></td>
      <td><pre>status: 400
Content-Type: application/problem+json

{
&nbsp;&nbsp;&quot;detail&quot; : &quot;exception&quot;,
&nbsp;&nbsp;&quot;instance&quot; : &quot;/flux-extended-problem-detail/response-status-exception&quot;,
&nbsp;&nbsp;&quot;status&quot; : 400,
&nbsp;&nbsp;&quot;title&quot; : &quot;Bad Request&quot;
}</pre></td>
    </tr>
        <tr>
      <td>20</td>
      <td><code>org.springframework.web.server.ContentTooLargeException</code><br><sub>extends ResponseStatusException → ErrorResponseException</sub><br><code>POST /flux-extended-problem-detail/content-too-large-exception</code><br><pre>Body: &quot;x&quot; × 1048576</pre></td>
      <td><pre>status: 413
Content-Type: application/problem+json

{
&nbsp;&nbsp;&quot;instance&quot; : &quot;/flux-extended-problem-detail/content-too-large-exception&quot;,
&nbsp;&nbsp;&quot;status&quot; : 413,
&nbsp;&nbsp;&quot;title&quot; : &quot;Content Too Large&quot;
}</pre></td>
    </tr>
        <tr>
      <td>21</td>
      <td><code>org.springframework.web.accept.InvalidApiVersionException</code><br><sub>extends ResponseStatusException → ErrorResponseException</sub><br><code>GET /flux-extended-problem-detail/invalid-api-version-exception</code><br><pre>Header: API-Version=3; requires `spring.webflux.apiversion.*`</pre></td>
      <td><pre>status: 400
Content-Type: application/problem+json

{
&nbsp;&nbsp;&quot;detail&quot; : &quot;Invalid API version: &#x27;3.0.0&#x27;.&quot;,
&nbsp;&nbsp;&quot;instance&quot; : &quot;/flux-extended-problem-detail/invalid-api-version-exception&quot;,
&nbsp;&nbsp;&quot;status&quot; : 400,
&nbsp;&nbsp;&quot;title&quot; : &quot;Bad Request&quot;
}</pre></td>
    </tr>
        <tr>
      <td>22</td>
      <td><code>org.springframework.web.accept.MissingApiVersionException</code><br><sub>extends ResponseStatusException → ErrorResponseException</sub><br><code>GET /flux-extended-problem-detail/missing-api-version-exception</code><br><pre>Requires `spring.webflux.apiversion.*`; API-Version not sent</pre></td>
      <td><pre>status: 400
Content-Type: application/problem+json

{
&nbsp;&nbsp;&quot;detail&quot; : &quot;API version is required.&quot;,
&nbsp;&nbsp;&quot;instance&quot; : &quot;/flux-extended-problem-detail/missing-api-version-exception&quot;,
&nbsp;&nbsp;&quot;status&quot; : 400,
&nbsp;&nbsp;&quot;title&quot; : &quot;Bad Request&quot;
}</pre></td>
    </tr>
        <tr>
      <td>23</td>
      <td><code>org.springframework.web.accept.NotAcceptableApiVersionException</code><br><sub>extends InvalidApiVersionException → ResponseStatusException</sub><br><code>GET /not-acceptable-api-version</code><br><pre>Header: API-Version=2; requires `spring.webflux.apiversion.*`</pre></td>
      <td><pre>status: 400
Content-Type: application/problem+json

{
&nbsp;&nbsp;&quot;detail&quot; : &quot;Invalid API version: &#x27;2.0.0&#x27;.&quot;,
&nbsp;&nbsp;&quot;instance&quot; : &quot;/not-acceptable-api-version&quot;,
&nbsp;&nbsp;&quot;status&quot; : 400,
&nbsp;&nbsp;&quot;title&quot; : &quot;Bad Request&quot;
}</pre></td>
    </tr>
        <tr>
      <td>24</td>
      <td><code>org.springframework.web.reactive.resource.NoResourceFoundException</code><br><sub>extends ResponseStatusException → ErrorResponseException</sub><br><code>GET /flux-extended-problem-detail/no-resource-found</code><br><pre>None</pre></td>
      <td><pre>status: 404
Content-Type: application/problem+json

{
&nbsp;&nbsp;&quot;detail&quot; : &quot;No static resource flux-extended-problem-detail/no-resource-found.&quot;,
&nbsp;&nbsp;&quot;instance&quot; : &quot;/flux-extended-problem-detail/no-resource-found&quot;,
&nbsp;&nbsp;&quot;status&quot; : 404,
&nbsp;&nbsp;&quot;title&quot; : &quot;Not Found&quot;
}</pre></td>
    </tr>
        <tr>
      <td>25</td>
      <td><code>org.springframework.web.server.PayloadTooLargeException</code><br><sub>extends ResponseStatusException → ErrorResponseException</sub><br><code>POST /flux-extended-problem-detail/payload-too-large-exception</code><br><pre>Body: text</pre></td>
      <td><pre>status: 413
Content-Type: application/problem+json

{
&nbsp;&nbsp;&quot;instance&quot; : &quot;/flux-extended-problem-detail/payload-too-large-exception&quot;,
&nbsp;&nbsp;&quot;status&quot; : 413,
&nbsp;&nbsp;&quot;title&quot; : &quot;Content Too Large&quot;
}</pre></td>
    </tr>
        <tr>
      <td>26</td>
      <td><code>org.springframework.web.ErrorResponseException</code><br><code>GET /flux-extended-problem-detail/error-response-exception</code><br><pre>None</pre></td>
      <td><pre>status: 400
Content-Type: application/problem+json

{
&nbsp;&nbsp;&quot;detail&quot; : &quot;Error details&quot;,
&nbsp;&nbsp;&quot;errors&quot; : [
&nbsp;&nbsp;&nbsp;&nbsp;{
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&quot;type&quot; : &quot;BUSINESS&quot;,
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&quot;target&quot; : null,
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&quot;message&quot; : &quot;Error message 1&quot;
&nbsp;&nbsp;&nbsp;&nbsp;},
&nbsp;&nbsp;&nbsp;&nbsp;{
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&quot;type&quot; : &quot;BUSINESS&quot;,
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&quot;target&quot; : null,
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&quot;message&quot; : &quot;Error message 2&quot;
&nbsp;&nbsp;&nbsp;&nbsp;}
&nbsp;&nbsp;],
&nbsp;&nbsp;&quot;instance&quot; : &quot;/flux-extended-problem-detail/error-response-exception&quot;,
&nbsp;&nbsp;&quot;status&quot; : 400,
&nbsp;&nbsp;&quot;title&quot; : &quot;Error title&quot;
}</pre></td>
    </tr>
        <tr>
      <td>27</td>
      <td><code>io.github.sbracely.extended.problem.detail.webflux.example.exception.FluxExtendedErrorResponseException</code><br><sub>extends ErrorResponseException</sub><br><code>GET /flux-extended-problem-detail/extended-error-response-exception</code><br><pre>None</pre></td>
      <td><pre>status: 500
Content-Type: application/problem+json

{
&nbsp;&nbsp;&quot;detail&quot; : &quot;Payment failed details&quot;,
&nbsp;&nbsp;&quot;errors&quot; : [
&nbsp;&nbsp;&nbsp;&nbsp;{
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&quot;type&quot; : &quot;BUSINESS&quot;,
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&quot;target&quot; : null,
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&quot;message&quot; : &quot;Insufficient balance&quot;
&nbsp;&nbsp;&nbsp;&nbsp;},
&nbsp;&nbsp;&nbsp;&nbsp;{
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&quot;type&quot; : &quot;BUSINESS&quot;,
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&quot;target&quot; : null,
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&quot;message&quot; : &quot;Payment frequent&quot;
&nbsp;&nbsp;&nbsp;&nbsp;}
&nbsp;&nbsp;],
&nbsp;&nbsp;&quot;instance&quot; : &quot;/flux-extended-problem-detail/extended-error-response-exception&quot;,
&nbsp;&nbsp;&quot;status&quot; : 500,
&nbsp;&nbsp;&quot;title&quot; : &quot;Payment failed title&quot;
}</pre></td>
    </tr>
        <tr>
      <td>28</td>
      <td><code>org.springframework.validation.method.MethodValidationException</code><br><code>GET /flux-extended-problem-detail/method-validation-exception</code><br><pre>None</pre></td>
      <td><pre>status: 500
Content-Type: application/problem+json

{
&nbsp;&nbsp;&quot;detail&quot; : &quot;Validation failed&quot;,
&nbsp;&nbsp;&quot;errors&quot; : [
&nbsp;&nbsp;&nbsp;&nbsp;{
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&quot;type&quot; : &quot;PARAMETER&quot;,
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&quot;target&quot; : &quot;name&quot;,
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&quot;message&quot; : &quot;name must not be null&quot;
&nbsp;&nbsp;&nbsp;&nbsp;},
&nbsp;&nbsp;&nbsp;&nbsp;{
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&quot;type&quot; : &quot;PARAMETER&quot;,
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&quot;target&quot; : &quot;name&quot;,
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&quot;message&quot; : &quot;name must not be blank&quot;
&nbsp;&nbsp;&nbsp;&nbsp;},
&nbsp;&nbsp;&nbsp;&nbsp;{
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&quot;type&quot; : &quot;PARAMETER&quot;,
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&quot;target&quot; : &quot;name&quot;,
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&quot;message&quot; : &quot;Name cannot be blank&quot;
&nbsp;&nbsp;&nbsp;&nbsp;},
&nbsp;&nbsp;&nbsp;&nbsp;{
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&quot;type&quot; : &quot;PARAMETER&quot;,
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&quot;target&quot; : &quot;age&quot;,
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&quot;message&quot; : &quot;Age cannot be null&quot;
&nbsp;&nbsp;&nbsp;&nbsp;},
&nbsp;&nbsp;&nbsp;&nbsp;{
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&quot;type&quot; : &quot;PARAMETER&quot;,
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&quot;target&quot; : &quot;name&quot;,
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&quot;message&quot; : &quot;Name length must be between 6-10&quot;
&nbsp;&nbsp;&nbsp;&nbsp;},
&nbsp;&nbsp;&nbsp;&nbsp;{
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&quot;type&quot; : &quot;PARAMETER&quot;,
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&quot;target&quot; : &quot;confirmPassword&quot;,
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&quot;message&quot; : &quot;Password and confirm password do not match&quot;
&nbsp;&nbsp;&nbsp;&nbsp;},
&nbsp;&nbsp;&nbsp;&nbsp;{
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&quot;type&quot; : &quot;PARAMETER&quot;,
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&quot;target&quot; : &quot;password&quot;,
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&quot;message&quot; : &quot;Password and confirm password do not match&quot;
&nbsp;&nbsp;&nbsp;&nbsp;},
&nbsp;&nbsp;&nbsp;&nbsp;{
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&quot;type&quot; : &quot;PARAMETER&quot;,
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&quot;target&quot; : null,
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&quot;message&quot; : &quot;Name is not valid&quot;
&nbsp;&nbsp;&nbsp;&nbsp;}
&nbsp;&nbsp;],
&nbsp;&nbsp;&quot;instance&quot; : &quot;/flux-extended-problem-detail/method-validation-exception&quot;,
&nbsp;&nbsp;&quot;status&quot; : 500,
&nbsp;&nbsp;&quot;title&quot; : &quot;Internal Server Error&quot;
}</pre></td>
    </tr>
  </tbody>
</table>
