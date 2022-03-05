import stanford.karel.SuperKarel;

import java.util.ArrayList;
import java.util.Collections;

public class Homework extends SuperKarel
{
    private int current_row = 1;
    private int current_column = 1;
    private int steps = 0;
    private int map_size = 0;
    private ArrayList<Point> points = new ArrayList<>();
    private int desiredPointsSize = 0;

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
        desiredPointsSize = points.size();
        System.out.println("Number of Desired points = " + desiredPointsSize);
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

    private Path getBestPath()
    {
        ArrayList<Point>until_now = new ArrayList<>();
        until_now.add(new Point(current_row,current_column));
        Path base_path = new Path(until_now,points);

        ArrayList<Path>frontier = new ArrayList<>();
        ArrayList<Path>explored = new ArrayList<>();
        frontier.add(base_path);
        while (frontier.size()!=0)
        {
            Collections.sort(frontier);
            Path current_state =  frontier.get(0);
            frontier.remove(0);
            explored.add(current_state);
            if(current_state.getSizeOfMustVisit()==0)
            {
                if(current_state.isFinished())
                {
                    return current_state;
                }
                else
                {
                    current_state.addMustVisitPoint(new Point(1,1));
                }
            }

            ArrayList<Path> children = current_state.getChildren();
            for (Path child:children)
            {
                if(!isPathHere(child,explored) && !isPathHere(child,frontier))
                {
                    frontier.add(child);
                }
            }
        }
        return null;
    }

    private boolean isPathHere(Path path,ArrayList<Path> pathArrayList)
    {
        for (Path p:pathArrayList)
        {
            if(path.equals(p))
            {
                return true;
            }
        }
        return false;
    }

    private void solution2()
    {
        setBeepersInBag(1000);
        initializeVariables();
        findSize("up");
        //findSize("right");
        findDesiredPoints();
        Path chosen_path = getBestPath();
        ArrayList<Point> chosen_road = chosen_path.getPath_points();
        points=chosen_road;
        points.remove(0);
        points.remove(points.size()-1);
        while (points.size()!=0)
        {
            goToPoint(points.get(0).getX(),points.get(0).getY());
        }
        goToPoint(1,1);
        System.out.println("steps = "  + steps);
        System.out.println("Estimated Cost = " + chosen_path.getPath_cost() + "\n");
    }

    public void run()
    {
        //solution1();
        solution2();
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

    class Path implements Comparable<Path>
    {
        private ArrayList<Point>path_points;
        private ArrayList<Point>must_visit;
        private double path_cost;

        public ArrayList<Point> getPath_points()
        {
            return path_points;
        }

        public boolean isFinished()
        {
            if(this.getSizeOfMustVisit()==0)
            {
                if(this.path_points.get(this.path_points.size()-1).equals( new Point(1,1)))
                {
                    return true;
                }
            }
            return false;
        }

        public double getPath_cost()
        {
            return path_cost;
        }

        public int getSizeOfMustVisit()
        {
            return this.must_visit.size();
        }

        Path(ArrayList<Point>path_points, ArrayList<Point>must_visit)
        {
            this.must_visit = must_visit;
            this.path_points = path_points;
            path_cost =0;
            updateAllCost();
        }

        public void addMustVisitPoint(Point point)
        {
            this.must_visit.add(point);
            updateAllCost();
            removeVisitedPoints();
        }

        public void addPoint(Point point)
        {
            path_points.add(point);
            updateAllCost();
            removeVisitedPoints();
        }

        private void removeVisitedPoints()
        {
            for (Point point:this.path_points)
            {
                for (int i= 0;i<this.must_visit.size();i++)
                {
                    if(this.path_points.get(this.path_points.size()-1).equals(this.must_visit.get(i)))
                    {
                        this.must_visit.remove(i);
                        break;
                    }
                }
            }
        }

        private void updateAllCost()
        {
            this.path_cost=0;
            double h1 = 0;
            for (int i = 0; i < this.path_points.size() -1; i++)
            {
                h1 += findCostBetweenTwoPoints(this.path_points.get(i),this.path_points.get(i+1));
            }
            h1/=398;
            h1*=100;
            double h2 = 1 - ((double)this.must_visit.size())/desiredPointsSize;
            h2*=100;
            this.path_cost = (h1 + h2);
        }

        private int findCostBetweenTwoPoints(Point point1,Point point2)
        {
            return Math.abs(point1.getX()-point2.getX()) + Math.abs(point1.getY() - point2.getY());
        }

        private void calculateCostsForMustVisit()
        {
            Point my_place = this.path_points.get(this.path_points.size()-1);
            for (Point p: this.must_visit)
            {
                p.calculateCostFrom(my_place.getX(), my_place.getY());
            }
            Collections.sort(this.must_visit);
        }

        public ArrayList<Path> getChildren()
        {
            int maximum_branching_factor = 4;
            if(this.path_points.size()>6)
            {
                maximum_branching_factor =1;
            }
            calculateCostsForMustVisit();
            int number_of_equal_choices = 1;
            for (int i = 0; i < this.must_visit.size()-1; i++)
            {
                if(this.must_visit.get(i).cost==this.must_visit.get(i+1).cost)
                {
                    number_of_equal_choices++;
                }
                else
                {
                    break;
                }
            }
            int number_of_copies = Math.min(Math.min(this.must_visit.size(), maximum_branching_factor),number_of_equal_choices);
            ArrayList<ArrayList<Point>> copies_of_target = new ArrayList<>();
            for (int i = 0; i < number_of_copies; i++)
            {
                copies_of_target.add(new ArrayList<>());
                copy_points(this.must_visit, copies_of_target.get(i));
            }
            ArrayList<ArrayList<Point>> copies_of_path = new ArrayList<>();
            for (int i = 0; i < number_of_copies; i++)
            {
                copies_of_path.add(new ArrayList<>());
                copy_points(this.path_points, copies_of_path.get(i));
            }
            ArrayList<Path> new_paths = new ArrayList<>();
            for (int i = 0; i < number_of_copies; i++)
            {
                new_paths.add(new Path(copies_of_path.get(i),copies_of_target.get(i)));
            }

            for (int i = 0; i < number_of_copies; i++)
            {
                new_paths.get(i).addPoint(new Point(this.must_visit.get(i).getX(),this.must_visit.get(i).getY()));
            }

            return  new_paths;
        }

        private void copy_points(ArrayList<Point>original , ArrayList<Point>new_one)
        {
            for (Point point:original)
            {
                new_one.add(new Point(point.getX(),point.getY()));
            }
        }

        @Override public int compareTo(Path other_path)
        {
            double compare_cost = ((Path)other_path).getPath_cost();
            //  For Ascending order
            return (int) (this.path_cost - compare_cost);
            // For Descending order do like this
            // return compare_cost-this.cost;
        }

        @Override
        public boolean equals(Object o)
        {
            if (this == o) return true;
            if (!(o instanceof Path)) return false;

            Path path = (Path) o;

            return path_points.equals(path.path_points);
        }

        @Override
        public int hashCode()
        {
            return path_points.hashCode();
        }
    }
}