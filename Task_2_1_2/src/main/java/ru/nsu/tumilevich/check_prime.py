def is_prime(n):
    if n <= 1: return False
    for i in range(2, int(n**0.5) + 1):
        if n % i == 0:
            return False, i
    return True, None

print(is_prime(999999937))
