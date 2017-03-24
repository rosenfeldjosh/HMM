package model;

import java.util.Random;

import control.EstimatorInterface;

public class Localizer implements EstimatorInterface {

	private int rows, cols, head;
	private int[] truePos;
	private double[] f;
	private double[][] T;

	public Localizer(int rows, int cols, int head) {
		this.rows = rows;
		this.cols = cols;
		this.head = head;
		Random random = new Random();
		truePos = new int[2];
		truePos[0] = random.nextInt(rows) / 2;
		truePos[1] = random.nextInt(cols) / 2;
		f = new double[rows * cols * 4];
		T = new double[rows * cols * 4][rows * cols * 4];
		T = buildT();
		double initValue = 1;
		initValue = initValue / (rows * cols * 4);
		for (int x = 0; x < rows * cols * 4; x++) {
			f[x] = initValue;
		}
	}

	private double[][] buildT() {
		double[][] T = new double[rows * cols * 4][rows * cols * 4];
		int i = 0;
		for (int x1 = 0; x1 < rows; x1++) {
			for (int y1 = 0; y1 < cols; y1++) {
				for (int h1 = 0; h1 < 4; h1++) {
					int j = 0;
					for (int x = 0; x < rows; x++) {
						for (int y = 0; y < cols; y++) {
							for (int h = 0; h < 4; h++) {
								T[i][j] = getTProb(x1, y1, h1, y, x, h);
								j++;
							}

						}
					}
					i++;
				}
			}
		}

		return T;
	}

	public int getNumRows() {
		return rows;
	}

	public int getNumCols() {
		return cols;
	}

	public int getNumHead() {
		return head;
	}

	public double getTProb(int y, int x, int h, int nY, int nX, int nH) {
		if (Math.abs(x - nX) == 1 && Math.abs(y - nY) == 0 || Math.abs(y - nY) == 1 && Math.abs(x - nX) == 0) {
			int corner = locatedInCorner(x, y);
			int wall = locatedNextToWall(x, y);
			if (nY - y == -1 && nH == 0) {// going NORTH
				if (corner != -1) {
					if (h == corner || (h + 1) % 4 == corner) {
						return 0.5;
					} else {
						if (h == 0) {
							return 0.7;
						} else {
							return 0.3;
						}
					}
				} else if (wall != -1) {
					if (h == wall) {
						return 0.333;
					}
					if (h == 0) {
						return 0.7;

					} else {
						return 0.15;
					}
				} else {
					if (h == 0) {
						return 0.7;
					} else {
						return 0.1;
					}
				}

				// return 1;
			} else if (nX - x == 1 && nH == 1) {// going EAST
				if (corner != -1) {
					if (h == corner || (h + 1) % 4 == corner) {
						return 0.5;
					} else {
						if (h == 1) {
							return 0.7;
						} else {
							return 0.3;
						}
					}
				} else if (wall != -1) {
					if (h == wall) {
						return 0.333;
					}
					if (h == 1) {
						return 0.7;

					} else {
						return 0.15;
					}
				} else {
					if (h == 1) {
						return 0.7;
					} else {
						return 0.1;
					}
				}
				// return 2;
			} else if (nY - y == 1 && nH == 2) {// going SOUTH
				if (corner != -1) {
					if (h == corner || (h + 1) % 4 == corner) {
						return 0.5;
					} else {
						if (h == 2) {
							return 0.7;
						} else {
							return 0.3;
						}
					}
				} else if (wall != -1) {
					if (h == wall) {
						return 0.333;
					}
					if (h == 2) {
						return 0.7;

					} else {
						return 0.15;
					}
				} else {
					if (h == 2) {
						return 0.7;
					} else {
						return 0.1;
					}
				}
				// return 3;
			} else if (nX - x == -1 && nH == 3) {// going WEST
				if (corner != -1) {
					if (h == corner || (h + 1) % 4 == corner) {
						return 0.5;
					} else {
						if (h == 3) {
							return 0.7;
						} else {
							return 0.3;
						}
					}
				} else if (wall != -1) {
					if (h == wall) {
						return 0.333;
					}
					if (h == 3) {
						return 0.7;

					} else {
						return 0.15;
					}
				} else {
					if (h == 3) {
						return 0.7;
					} else {
						return 0.1;
					}
				}
				// return 4;
			}
		}
		return 0.0;
	}

	private int locatedNextToWall(int x, int y) {

		if (!isNotOutOfBounds(y - 1, x)) {
			return 0;
		} else if (!isNotOutOfBounds(y, x + 1)) {
			return 1;
		} else if (!isNotOutOfBounds(y + 1, x)) {
			return 2;
		} else if (!isNotOutOfBounds(y, x - 1)) {
			return 3;
		} else {
			return -1;
		}
	}

	private int locatedInCorner(int x, int y) {

		if (!isNotOutOfBounds(y - 1, x) && !isNotOutOfBounds(y, x - 1)) {
			return 0;
		} else if (!isNotOutOfBounds(y, x + 1) && !isNotOutOfBounds(y - 1, x)) {
			return 1;
		} else if (!isNotOutOfBounds(y, x + 1) && !isNotOutOfBounds(y + 1, x)) {
			return 2;
		} else if (!isNotOutOfBounds(y + 1, x) && !isNotOutOfBounds(y, x - 1)) {
			return 3;
		} else {
			return -1;
		}

	}

	public double getOrXY(int rX, int rY, int x, int y) {
		if (rX < 0 || rY < 0) {
			return nothingSum(x, y);
		} else if (Math.abs(x - rX) > 2 || Math.abs(y - rY) > 2) {
			return 0.0;
		} else if (Math.abs(x - rX) == 2 || Math.abs(y - rY) == 2) {
			return 0.025;
		} else if (Math.abs(x - rX) == 1 || Math.abs(y - rY) == 1) {
			return 0.05;
		} else {
			return 0.1;
		}

	}

	private double nothingSum(int x, int y) {
		double negativeSum = 0;
		for (int i = 0; i < 6; i++) {
			for (int j = 0; j < 6; j++) {
				if (isNotOutOfBounds(y - 3 + i, x - 3 + j)) {
					negativeSum += getOrXY(x - 3 + j, y - 3 + i, x, y);
				}
			}
		}
		return 1 - negativeSum;
	}

	public int[] getCurrentTruePosition() {
		return truePos;

	}

	public int[] getCurrentReading() {
		int[] truePos = getCurrentTruePosition();
		double randomizer = Math.random();
		double diceSum = 0;
		int[] ret = new int[2];
		for (int i = 0; i < 5; i++) {
			for (int j = 0; j < 5; j++) {
				if (isNotOutOfBounds(truePos[0] - 3 + i, truePos[1] - 3 + j)) {
					double currentPosProb = getOrXY(truePos[1] - 3 + j, truePos[0] - 3 + i, truePos[1], truePos[0]);
					if (randomizer >= diceSum && randomizer < diceSum + currentPosProb) {
						ret[0] = truePos[0] - 3 + i;
						ret[1] = truePos[1] - 3 + j;
						return ret;
					}
					diceSum += currentPosProb;
				}
			}
		}
		ret[0] = -1;
		ret[1] = -1;
		return ret;
	}

	private boolean isNotOutOfBounds(int i, int j) {
		return i > -1 && i < rows && j > -1 && j < cols;
	}

	public double getCurrentProb(int x, int y) {
		int i = 16 * y + 4 * x;
		return f[i] + f[i + 1] + f[i + 2] + f[i + 3];
	}

	public void update() {
		moveRobot();
		updatef();
	}

	/*
	 * Sets the fMatrix values to the next step based on new sensor information
	 * and transition matrix
	 */
	private void updatef() {
		double[][] O = buildO();

		// for(int i = 0; i < O.length;i++)
		// {
		// for(int j =0; j < O.length;j++)
		// {
		// System.out.print(O[i][j]+" ");
		// }
		// System.out.println();
		// }
		f = vectMult(matrixMult(O, transpose(T)), f);
		f = norm(f);
	}

	private double[] norm(double[] vec) {
		double sum = 0.0;
		for (int i = 0; i < vec.length; i++) {
			sum += vec[i];
		}
		if (sum != 1) {
			for (int i = 0; i < vec.length; i++) {
				vec[i] = vec[i] / sum;
			}
		}
		return vec;
	}

	private double[] vectMult(double[][] OT, double[] f2) {
		double[] retVec = f2;
		for (int i = 0; i < rows * cols * 4; i++) {
			for (int j = 0; j < rows * cols * 4; j++) {
				retVec[i] += retVec[i] * OT[i][j];
			}
		}
		return retVec;
	}

	private double[][] transpose(double[][] t2) {
		double[][] tempMat = t2;
		double[][] retTemp = t2;
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				retTemp[i][j] = tempMat[j][i];
			}
		}
		return retTemp;
	}

	private double[][] matrixMult(double[][] o, double[][] t2) {

		double[][] matrix1 = o;

		double[][] matrix2 = t2;

		double[][] product = new double[rows * cols * 4][rows * cols * 4];

		for (int i = 0; i < rows * cols * 4; i++) {
			for (int j = 0; j < rows * cols * 4; j++) {
				for (int k = 0; k < rows * cols * 4; k++) {
					product[i][j] += matrix1[i][k] * matrix2[k][j];
				}
			}
		}

		return product;
	}

	private double[][] buildO() {
		double[][] O = new double[rows * cols * 4][rows * cols * 4];
		int i = 0;
		for (int x1 = 0; x1 < rows; x1++) {
			for (int y1 = 0; y1 < cols; y1++) {
				for (int h1 = 0; h1 < 4; h1++) {
					int j = 0;
					for (int x = 0; x < rows; x++) {
						for (int y = 0; y < cols; y++) {
							for (int h = 0; h < 4; h++) {
								if (i == j) {
									O[i][j] = getOrXY(x, y, truePos[0], truePos[1]);
								} else {
									O[i][j] = 0.0;
								}
								j++;
							}

						}
					}
					i++;
				}
			}
		}

		return O;
	}

	/* Moves true position of robot based on transition percentages */
	private void moveRobot() {

		Random generator = new Random();
		double number = generator.nextDouble();
		double diceSum = 0.0;
		for (int x = 0; x < rows; x++) {
			for (int y = 0; y < cols; y++) {
				for (int h = 0; h < 4; h++) {
					diceSum += getTProb(truePos[1], truePos[0], head, y, x, h);
					if (diceSum >= number) {
						truePos[0] = x;
						truePos[1] = y;
						head = h;
						return;
					}
				}
			}
		}
	}
}
