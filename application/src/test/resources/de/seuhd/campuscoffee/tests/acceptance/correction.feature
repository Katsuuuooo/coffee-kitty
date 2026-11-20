Feature: User coffee correction
  An admin corrects the coffee count of a user.

  Scenario: An admin corrects the coffee count of a user
    Given the admin "jane_doe" and the user "maxmustermann" with a coffee count of 3 and a note "Double tap on 16.11."
    When the admin corrects the coffee count to 2 with the note
    Then the request succeeds and the coffee count is 2, and the corrections list contain before, after, note and admin

  Scenario Outline: An invalid correction of the coffee count of a user
    Given the user "maxmustermann" with a coffee count of 3
    When <corrector> tries to correct the users coffee count to <correction> with the note <note>
    Then the request returns <return>

    Examples:
      | corrector     | note                   | correction | return          |
      | maxmustermann | "Double tap on 16.11." | 2          | 403 Forbidden   |
      | jane_doe      | ""                     | 2          | 400 Bad Request |
      | jane_doe      | "Double tap on 16.11." | -2         | 400 Bad Request |

  Scenario: An user fails to undo last coffee after an admin correction
    Given the user "maxmustermann" has 2 coffees, the last one added at 09:00:00
    And the user adds a coffee at 10:00:00
    Then the admin "jane_doe" corrects the coffee count to 2 at 10:02:00
    When the user undoes the coffee at 10:03:00
    Then the request returns is 409 Conflict and the coffee count stays 2
