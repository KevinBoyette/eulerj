/**
 * https://projecteuler.net/problem=4
 *
 * <p>A palindromic number reads the same both ways. The largest palindrome made
 * from the product of two 2-digit numbers is 9009 = 91 x 99.
 *
 * <p>Find the largest palindrome made from the product of two 3-digit numbers.
 */
public class Problem00004 {
  record DigitRange(long low, long high) {
    DigitRange {
      if (low < 1 || low > high) {
        throw new IllegalArgumentException(
            "invalid range [" + low + ", " + high + "]");
      }
    }

    /** Requires 1 <= digits <= 9 (larger values overflow 64-bit products). */
    static DigitRange of(int digits) {
      if (digits < 1 || digits > 9) {
        throw new IllegalArgumentException(
            "digits must be in [1, 9], got " + digits);
      }

      long high = 1;
      for (int i = 0; i < digits; ++i) {
        high *= 10;
      }
      return new DigitRange(high / 10, high - 1);
    }
  }

  static boolean isPalindrome(long number) {
    long original = number;
    long reversed = 0;

    while (number > 0) {
      reversed = reversed * 10 + number % 10;
      number /= 10;
    }
    return reversed == original;
  }

  static long largestPalindromeProduct(int digits) {
    if (digits == 1) {
      return 9;
    }

    var r = DigitRange.of(digits);
    long largest = 0;

    // NOTE(kevin): all even length palindromes are multiples of 11.
    // That means one of the two factors is 11.
    // Since we cannot exchange the 2 factors anymore, b must cover
    // the full range.
    //
    // 10 is congruent to -1 (mod 11)
    for (long a = r.high() - r.high() % 11; a >= r.low(); a -= 11) {
      if (a * r.high() <= largest) {
        break;
      }
      for (long b = r.high(); b >= r.low(); --b) {
        long p = a * b;
        if (p <= largest) {
          break;
        }
        if (isPalindrome(p)) {
          largest = p;
        }
      }
    }
    return largest;
  }

  private static void check(boolean condition, String message) {
    if (!condition) {
      throw new AssertionError(message);
    }
  }

  public static void main(String[] args) {
    check(isPalindrome(9009), "9009 is a palindrome");
    check(!isPalindrome(9001), "9001 is not a palindrome");

    System.out.println(largestPalindromeProduct(2));
    check(largestPalindromeProduct(2) == 9009, "the example should pass");

    System.out.println(largestPalindromeProduct(3));
    check(
        largestPalindromeProduct(3) == 906609, "the answer should be correct");
  }
}
