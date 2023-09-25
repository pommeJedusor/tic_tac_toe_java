import java.util.Scanner;

class Node
{
    public final int WIDTH = 3;
    public final int HEIGHT = 3;
    public final int VOID = 0;
    public final int PLAYER1 = 1;
    public final int PLAYER2 = 2;

    int[] grid; //0 void, 1 player1, 2 player2
    int next_player;
    int deep;
    int move;
    int winning;//(for the player1)'='=0, 'V'=1, 'L'=2
    Node parent;
    Node[] children;

    Node(int[] grid, int previous_player, Node parent, int deep, int move)
    {
        this.grid = grid;
        this.next_player = previous_player%2+1;
        this.parent = parent;
        this.children = new Node[this.WIDTH*this.HEIGHT];
        this.deep = deep;
        this.move = move;
    }

    void see_grid()
    {
        for (int i=0;i<9;i++)
        {
            if (i%3==0)
            {
                System.out.println();
            }
            System.out.print(this.grid[i]);
        }
        System.out.println();
    }

    boolean[] get_moves()
    {
        boolean[] moves = new boolean[this.WIDTH*this.HEIGHT];
        for (int i=0;i<this.WIDTH*this.HEIGHT;i++)
        {
            if (this.grid[i]==this.VOID)
            {
                moves[i]=true;
            }
            else
            {
                moves[i]=false;
            }
        }
        return moves;
    }

    int[] play_move(int move)
    {
        int[] grid = new int[this.grid.length];

        for (int i=0;i<grid.length;i++)
        {
            if (i==move)
            {
                grid[i]=this.next_player;
            }
            else
            {
                grid[i]=this.grid[i];
            }
        }
        return grid;
    }

    boolean is_winning()
    {
        int player = this.next_player%2+1;
        int x_move = this.move%this.WIDTH;
        int y_move = this.move/this.HEIGHT;
        //vertical
        boolean win_vertical = true;
        for (int i=0;i<this.HEIGHT;i++)
        {
            if (this.grid[i*this.WIDTH+x_move]!=player)
            {
                win_vertical=false;
                break;
            }
        }
        //horizontal
        boolean win_horizontal = true;
        for (int i=0;i<this.WIDTH;i++)
        {
            if (this.grid[y_move*this.WIDTH+i]!=player)
            {
                win_horizontal=false;
                break;
            }
        }
        //diagonal left top -> down right
        boolean win_diagonal1 = true;
        for (int i=0;i<this.WIDTH;i++)
        {
            if (this.grid[i*this.WIDTH+i]!=player)
            {
                win_diagonal1=false;
                break;
            }
        }
        //diagonal down top -> top right
        boolean win_diagonal2 = true;
        for (int i=0;i<this.WIDTH;i++)
        {
            if (this.grid[(this.HEIGHT-i-1)*this.WIDTH+i]!=player)
            {
                win_diagonal2=false;
                break;
            }
        }
        if (win_vertical || win_horizontal || win_diagonal1 || win_diagonal2)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    int get_tree()
    {
        if (this.is_winning())
        {
            this.winning = 2;
            return 1;
        }
        else if(this.deep==this.WIDTH*this.HEIGHT)
        {
            return 0;
        }
        boolean[] moves = get_moves();
        int result;
        int best_result = 2;
        Node node;
        for (int i=0;i<this.WIDTH*this.HEIGHT;i++)
        {
            if (moves[i])
            {
                node = new Node(this.play_move(i),this.next_player,this,this.deep+1,i);
                this.children[i] = node;
                result = node.get_tree();
                if (result==1)
                {
                    this.winning=1;
                    return 2;
                }
                else if(result==0)
                {
                    best_result=0;
                }
            }
        }
        if (best_result==0)
        {
            this.winning=0;
            return 0;
        }
        else
        {
            this.winning=2;
            return 1;
        }
    }

    int get_best_move()
    {
        int result = this.get_tree();
        //System.out.println(result);
        for (int i=0;i<this.WIDTH*this.HEIGHT;i++)
        {
            if (this.children[i] != null)
            {
                if (result==0 && this.children[i].winning==0)
                {
                    return i;
                }
                else if(result==1 && this.children[i].winning==1)
                {
                    return i;
                }
                else if(result==2 && this.children[i].winning==2)
                {
                    return i;
                }
            }
        }
        return 666;
    }
}

class TicTacToe
{
    public static Scanner scanner = new Scanner(System.in);
    public static int get_move(int[] grid)
    {
        int input=-1;
        while (input<1 || input>9 || grid[input-1]!=0)
        {
            System.out.print("entrez votre coup: ");
            input = scanner.nextInt();
        }
        return input;
    }

    public static void game(int[] grid)
    {

        System.out.print("souhaitez vous commencer?(1 pour oui, 2 pour non)\n");
        int player = scanner.nextInt();
        int last_move=0;
        int input;
        Node node;
        int move;
        int deep=0;

        if (player==1)
        {
            input = get_move(grid);
            System.out.println("vous avez joué: "+input);
            grid[input-1]=player;
            last_move = input-1;
            deep++;
        }
        while (true)
        {
            //bot
            node = new Node(grid, player, null, deep, last_move);
            move = node.get_best_move();
            node.see_grid();
            grid[move]=player%2+1;
            last_move = move;
            deep++;
            System.out.println("je joue: "+(move+1));
            node.see_grid();
            node.next_player = node.next_player%2+1;
            if (node.is_winning())
            {
                System.out.println("j'ai gagné!");
                return;
            }
            else if(node.deep==8)
            {
                System.out.println("match nul");
                return;
            }

            //player
            input = get_move(grid);
            System.out.println("vous avez joué: "+input);
            grid[input-1]=player;
            last_move = input-1;
            deep++;
            if(node.deep==8)
            {
                System.out.println("match nul");
                return;
            }
        }        
    }

    public static void main(String[] args)
    {
        int[] grid = {0,0,0,0,0,0,0,0,0};
        TicTacToe.game(grid);
    }
}