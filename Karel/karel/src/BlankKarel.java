import stanford.karel.SuperKarel;

import java.util.ArrayList;
import java.util.Collections;

public class BlankKarel extends SuperKarel
{
	private int current_row = 1;
	private int current_column = 1;
	private int steps = 0;
	private int map_size = 0;
	private ArrayList<Point> points = new ArrayList<>();

	private void myMove()
	{
		if(facingWest())
		{
			current_column--;
		}
		else if(facingEast())
		{
			current_column++;
		}
		else if(facingSouth())
		{
			current_row--;
		}
		else if(facingNorth())
		{
			current_row++;
		}
		move();
		checkIfThisIsATarget();
		steps++;
	}

	private void findSize(String destination)
	{
		int count = 1;
		if(destination.toLowerCase() == "right")
		{
			while(!facingEast())
			{
				turnLeft();
			}
			while(!frontIsBlocked())
			{
				myMove();
				count++;
			}
		}
		else if (destination.toLowerCase()=="up")
		{
			while(!facingNorth())
			{
				turnLeft();
			}
			while(!frontIsBlocked())
			{
				myMove();
				count++;
			}
		}
		map_size = count;
		System.out.println("Map size = " + map_size);
	}

	private void findDesiredPoints()
	{
		int size_of_section;
		if(map_size %2==1)//if map size is odd
		{
			int line = (map_size /2)+1;
			for (int i = 1; i <= map_size; i++)
			{
				points.add(new Point(line,i));//add points located at the dividing row
				if(i==line)
				{
					continue;
				}
				points.add(new Point(i,line));//add points located at the dividing column
			}
			size_of_section = (map_size - 1)/2;
		}
		else//if map size is even
		{
			int line1 = map_size /2;
			int line2 = (map_size /2) +1;
			for (int i = 1; i <= map_size; i++)
			{
				points.add(new Point(line1,i));
				points.add(new Point(line2,i));
				if(i==line1 || i==line2)
				{
					continue;
				}
				points.add(new Point(i,line1));
				points.add(new Point(i,line2));
			}
			size_of_section = (map_size - 2)/2;
		}

		//finding the center of the four sections
		if(size_of_section%2==1)//if odd
		{
			int h1 = (size_of_section/2)+1;
			int h2 = map_size - (size_of_section/2);

			points.add(new Point(h1,h1));
			points.add(new Point(h1,h2));
			points.add(new Point(h2,h1));
			points.add(new Point(h2,h2));
		}
		else//if even
		{
			int h1 = size_of_section/2;
			int h2 = (size_of_section/2)+1;
			int h3 = map_size - (size_of_section/2);
			int h4 = map_size - (size_of_section/2) +1;
			int h[] = new int[] {h1,h2,h3,h4};
			for (int i:h)
			{
				for (int j:h)
				{
					points.add(new Point(i,j));
				}
			}
		}
	}

	private void goToRow(int desired_row)
	{
		int h =  desired_row - current_row;
		if (h==0)
		{
			checkIfThisIsATarget();
			return;
		}
		else if(h>0)//if the row above me
		{
			while (!facingNorth())
			{
				turnLeft();
			}
			while (current_row != desired_row)
			{
				myMove();
			}
		}
		else//if the row below me
		{
			while (!facingSouth())
			{
				turnLeft();
			}
			while (current_row != desired_row)
			{
				myMove();
			}
		}
	}

	private void goToColumn(int desired_column)
	{
		int h =  desired_column - current_column;
		if (h==0)
		{
			checkIfThisIsATarget();
			return;
		}
		else if(h>0)//if the column on my right
		{
			while (!facingEast())
			{
				turnLeft();
			}
			while (desired_column !=current_column)
			{
				myMove();
			}
		}
		else//if the column on my left
		{
			while (!facingWest())
			{
				turnLeft();
			}
			while (current_column !=desired_column)
			{
				myMove();
			}
		}
	}

	private void goToPoint(int x,int y)
	{
		goToColumn(y);
		goToRow(x);
	}

	private void calculateCosts()
	{
		for (Point p: points)
		{
			p.calculateCostFrom(current_row,current_column);
		}
		Collections.sort(points);
	}

	private void checkIfThisIsATarget()
	{
		Point p = new Point(current_row,current_column);
		for (int i = 0;i<points.size();i++)
		{
			if(points.get(i).equals(p))
			{
				if(noBeepersPresent())
				{
					putBeeper();
				}
				points.remove(i);
				i--;
			}
		}
	}

	private void initializeVariables()
	{
		steps = 0;
		map_size = 0;
		current_row = 1;
		current_column = 1;
		points = new ArrayList<>();
	}

	private void solution1()
	{
		setBeepersInBag(1000);
		initializeVariables();
		findSize("up");
		//findSize("right");
		findDesiredPoints();
		while(points.size()!=0)
		{
			calculateCosts();
			goToPoint(points.get(0).getX(),points.get(0).getY());
		}
		goToPoint(1,1);
		System.out.println("steps = " + steps + "\n");
	}

	public void run()
	{
		solution1();
	}

	class Point implements Comparable<Point>
	{
		private int x;
		private int y;
		private int cost = 0;

		Point(int x,int y)
		{
			this.x = x;
			this.y=y;
		}

		public int getX()
		{
			return x;
		}

		public int getY()
		{
			return y;
		}

		public int getCost()
		{
			return cost;
		}

		public void calculateCostFrom(int row,int column)
		{
			this.cost = Math.abs(row - this.x) + Math.abs(column - this.y);
		}

		// overriding the compareTo method of Comparable class
		@Override public int compareTo(Point other_point)
		{
			int compare_cost = ((Point)other_point).getCost();
			//  For Ascending order
			return this.cost - compare_cost;
			// For Descending order do like this
			// return compare_cost-this.cost;
		}

		@Override
		public boolean equals(Object o)
		{
			if (this == o) return true;
			if (!(o instanceof Point)) return false;

			Point point = (Point) o;

			if (x != point.x) return false;
			return y == point.y;
		}

		@Override
		public int hashCode()
		{
			int result = x;
			result = 31 * result + y;
			return result;
		}
	}
}