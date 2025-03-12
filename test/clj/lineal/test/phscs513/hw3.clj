(ns lineal.test.phscs513.hw2
  (:require [clojure.test :refer :all]
            [uncomplicate.neanderthal.core :as nc]
            [uncomplicate.neanderthal.native :as nn]
            [uncomplicate.neanderthal.linalg :as la]
            [uncomplicate.neanderthal.math :as nm]
            [uncomplicate.fluokitten.core :as fc]
            [lineal.config :refer [env]]
            [lineal.norms :as n]))

(deftest hw1
  "Suppose you’re on a game show, and you’re given the choice of three doors: Behind one door is a car; behind the others, goats. You pick a door, say number 1, and the host, who knows what’s behind the doors, opens another door, say number 3, which has a goat. He then says to you, “Do you want to pick door number 2?” Is it to your advantage to switch your choice (assuming you would rather win a car than a goat)?"
  (is (= ))
  )

(deftest hw2
  "Prove Theorem 1.3.9 in Casella and Berger."
  (is (= 1 1))
  )

(deftest hw3
  "Show that the operation of taking an expectation value is a linear operator. If Y is a vector of random variables and T is a linear operator, show that Cov(T Y ) = T 0 Cov(Y )T where T 0 denotes the transpose of T ."
  (is (= ))
  )

(deftest hw4
  "We saw in class that the sum of many random variables converges to the normal distribution; however, the sum of uniform random variables converged much faster than the sum of exponential random variables. One reason for this is that the exponential distribution is skewed.  If a random variable X has mean µ and variance σ 2 , the skew (often denoted by γ 1 ) is * γ 1 = X − µ σ  3 + .  1. Show that for the uniform and normal distributions, γ 1 = 0.  2. What is the skew of the exponential distribution?  3. Numerically generate a random variable S n as the sum of n exponentially distributed random variables. Generate 1000 copies of S n and estimate its skew for n = 1, 5, 10, 20. Estimate how big must n be in order for the skew to be less that 0.1."
  (is (= ))
  )

(deftest hw5
  "Suppose X ∼ N (0, σ) for some unknown value of σ.
1. What is the MLE of σ? (Assume you have only one realization of X.)

2. What is the bias of the MLE of σ?

3. What is the variance for the MLE?

4. Can you construct an unbiased estimator of σ? What is the variance for your unbiased estimator?

The next several problems explore properties of parameters inferred by linear regression. These questions are mostly numerical problems to be done on the computer. Think of these as guided numerical experiments. Regression is a powerful tool, but there are lots of things to keep in mind when doing it. Although similar, each of these problems explores a different facet of the regression/parameter inference problem. So if you see these problems as repeating the same thing over and over, you are probably missing the point."

  (is (= ))
  )

(deftest hw6
  "Consider the least squares error function:
C(θ₁, θ₂) = ∑²⁰ᵢ₌₁ (f (tᵢ) − θ₁t − θ₂t²)²
where tᵢ are 20 evenly spaced between 0 and 1 and f(t) = t² .

1. Make a contour plot of C with θ 1 on the x-axis and θ 2 on the y axis.

2. Numerically find the Fisher Information Matrix (FIM) for this model. Find its eigenvalues and eigenvectors and graphically confirm that the ellipses in your contour plot are oriented with its eigenvectors and the aspect ratio is given by the ratio of the square root of the eigenvalues.

3. Now, generate data according to y i = f (t i ) +  i where  is Gaussian noise with zero mean and standard deviation 0.1. Using OLS, fit your data to the model θ 1 t + θ 2 t 2 .

4. Repeat your OLS fit 1000 times for different realizations of your data and plot your estimates as a scatter plot on top of your contour plot.

5. Calculate the covariance matrix from your estimates and compare this with the inverse of the Fisher Information Matrix.

6. Make a histogram of your estimates for θ 1 and θ 2 and plot on top of this the true distribution for θ 1 and θ 2 .
"
  (is (= ))
  )

(deftest hw7-in-class
  "Consider data generated according to y i = f (t i ) +  i for some function f (t) and where t i ’s are 20 equally spaced points between 0 and 1 and  are independent, normally distributed with zero mean and variance σ 2 .

1. If you fit this data by OLS (i.e., y = Xθ) using monomial basis functions (i.e., 1, t, t 2 , . . . , t n ), what should σ be if you want to estimate all the parameters in your model with a standard error no larger than 0.1 for the cases that n = 2, 5, 10?

2. Now, assume that σ = 0.1, how many times would you have to repeat your experiment so that the standard error in your parameters was no larger that 0.1?

3. Now, repeat both calculations using Legendre polynomials (up to order n = 2, 5, 10) as basis functions.

4. What can you conclude about the advantages of using monomials versus Legendre polynomials as basis functions?
"
  
  (is (= ))
  )

(deftest hw8
  "Suppose you have data generated according to y = f (t) +  where  is noise with zero
mean and variance σ 2 . Now suppose that you have used some method to generate an approximation
to f (t), denote this by f ˆ (t). This approximation could come from linear regression for example, but
it could even be something more sophisticated, like a neural network; for this problem it doesn’t
really matter. Show that the expected error can be decomposed as:
D
E

 2
(y − f ˆ (t)) 2 = Bias( f ˆ (t))
+ Var( f ˆ (t)) + σ 2 .
The above expression is known as the bias-variance decomposition. The last term is the irreducible
error. The first two terms are together known as the reducible error. Learning algorithms often
have “hyper-parameters” that tune the complexity of the model, for example, the number of basis
functions or some regularization parameter. Overly simple models often have high bias error (known
as under-fitting). In contrast, very complex models are often associated with large variance error
(known as over-fitting). You will explore both of these ideas in the next problem."
  (is (= ))
  )


(deftest hw9
  "Generate training data yᵢ = f (tᵢ) + εᵢ where there time points t are 20 equally spaced points between -1 and 1 and εᵢ are independently drawn from a normal distribution with mean zero and standard deviation 0.1."
  (let [ ]
    )


  (is (= ))
  )

(deftest hw10
  "Repeat the previous problem with f (t) = sin(12t). What is different about the bias/variance tradeoff when fitting this function?"
  (is (= ))
  )
