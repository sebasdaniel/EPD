/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ejerciciopd;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author 53B45
 */
public class CalculoRecursivo {
    
    public static final int CostFijo = 3;
    public static final int CostVar = 1;
    public static final float CostInvent = (float) 1/2;
    public static final int MaxProd = 5;
    public static final int MaxInvent = 4;
    private int[] DMD = {1, 3, 2, 4};
    private int inventFinal;
    private int inventInicial;
    private String salida;
    private int[][] Xt;
    private Nodo[][] nodos = new Nodo[5][5];
    
    public CalculoRecursivo(int inicio, int fin){
        
        salida = "";
        inventInicial = inicio;
        inventFinal = fin;
        DMD[3] = 4 + inventFinal;
        Xt = new int[4][5];
        
        int distX = 20;
        int distY = 20;
        
        //creamos los nodos y les asignamos sus respectivas pociciones
        for(int t=0; t<5; t++){
            
            for(int i=0; i<5; i++){
                
                nodos[t][i] = new Nodo(distX, distY);
                distY += 80;
            }
            
            distX += 110;
            distY = 20;
        }
    }
    
//    public void setFinalizacion(int fin){
//        
//        inventFinal = fin;
//    }
    
    private int costoUnidad(int x){
        
        //se puede validar que x sea menor de 6
        return (x == 0) ? 0 : (x + CostFijo);
    }
    
    public float costoMin(int t, int i){
        
        if(t > 4){
            
            return 0;
        }
        
        int inventario;
        
        List<Float> temp = new ArrayList<>();
        
        for(int x=0; x<=MaxProd; x++){
            
            inventario = (i + x - DMD[t - 1]);
            
            float result;
            
            if(inventario >= 0 && inventario <= 4){
                
                float cosSig = costoMin(t + 1, inventario);
                
                if(cosSig != -1){
                    
                    if(t == 4){
                        inventario += inventFinal;
                    }
                    
                    result = (CostInvent * inventario) + costoUnidad(x) + cosSig;
                    temp.add(result);
                }
                
            }
        }
        
        return (temp.size() > 0) ? buscarMenor(temp) : -1;
    }
    
    private float buscarMenor(List<Float> lista){
        
        float menor = lista.get(0);
        
        for(int i=1; i<lista.size(); i++){
            
            if(lista.get(i) < menor){
                
                menor = lista.get(i);
            }
        }
        
        return menor;
    }
    
    public void mostrarTabla(){
        
        int t;
        int i;
        int x;
        
        String linea = "+----+----+--------------+------+-------------+---------+\n";
        
        for(t=4; t>0; t--){
            
            salida += String.format("\n\n%36s\n", "TABLA PARA EL MES " + t);
            
            salida += linea;
            
            int demand = DMD[t - 1];
            
            if(t == 4){
                
                demand -= inventFinal;
            }
            
            salida += "|  i |  x | (1/2)(i+X-"+demand+") | C(X) |"
                    + "   F"+(t+1)+"(i+X-"+demand+") |    C.T. |\n";
            
            salida += linea;
            
            for(i=0; i<=MaxInvent; i++){
                
                float cost = costoMin(t, i);
                int xt = 0;
                
                for(x=0; x<=MaxProd; x++){
                    
                    int inventario = (i + x - DMD[t -1]);
                    
                    if(inventario >= 0 && inventario <= 4){
                        
                        float var1, var3, res;
                        int var2;
                        
                        var3 =  costoMin(t + 1, inventario);
                        
                        if(var3 != -1){
                            
                            if(t == 4){
                                inventario += inventFinal;
                            }
                            
                            var1 = (CostInvent * inventario);
                            var2 = costoUnidad(x);
                            
                            res = var1 + var2 + var3;
                            
                            salida += String.format("| %2d | %2d | %12.2f | %4d | %11.2f | %7.2f |\n", i, x,
                                var1, var2, var3, res);
                            
                            if(cost == res){
                                
                                xt = x;
                                Xt[t - 1][i] = xt;
                            }
                        }
                        
                    }
                    
                }
                
                if(cost != -1){
                    
                    salida += linea;
                    salida += String.format("| %25s - %10s", "F" + t + "(" + i + ") = " + cost,
                            "X" + t + "(" + i + ") = " + xt);
                    salida += "                |\n" + linea;
                }
                
            }
        }
    }
    
    public String obtenerResultado(){
        
        return salida;
    }
    
    public String crearCalendario(){
        
        int t, i, x, invent;
        float costTot = 0, temp;
        
        String lin = "+-----+-----+------+-----+------+\n";
        
        String tabla = "\nCALENDARIO OPTIMO DE PRODUCCION CON "+inventInicial+" UNID. EN INVENTARIO EN EL MES 1.\n\n";
        
        tabla += lin + "| Mes | DMD | Prod | Inv | C.T. |\n" + lin;
        
        i = inventInicial;
        
        for(t=1; t<=4; t++){
            
            int demand = DMD[t - 1];
            
            x = Xt[t - 1][i];
            invent = (i + x - demand);
            
            if(t == 4){
                
                invent += inventFinal;
                demand -= inventFinal;
            }
            
            temp = (costoUnidad(x) + (float) 0.5 * invent);
            
            tabla += String.format("| %3d | %3d | %4d | %3d | %4.1f |\n", t, demand, x, invent, temp);
            
            i = invent;
            costTot += temp;
        }
        
        tabla += lin + "|  Costo Total en Dolares| " + costTot + " |\n";
        tabla += "+------------------------+------+";
        
        return tabla;
        
    }
    
    public void pintarGrafo(Graphics g){
        
        int ini = inventInicial;
        int pos = 15;
        int x, invent;
        
        Graphics2D g2d = (Graphics2D) g;
        
        g2d.setStroke(new BasicStroke(2));
        
        g.setFont(new Font("SansSerif", Font.BOLD, 12));
        
        for(int t=0; t<4; t++){
            
            x = Xt[t][ini];
            invent = (ini + x - DMD[t]);
            
            if(t == 3){
                invent += inventFinal;
            }
            
            g.drawLine(nodos[t][ini].X()+pos, nodos[t][ini].Y()+pos, nodos[t+1][invent].X()+pos, nodos[t+1][invent].Y()+pos);
            
            g.drawString("" + ((float)(invent*0.5) + costoUnidad(x)), nodos[t][ini].X()+33, nodos[t][ini].Y()+20);
            
            ini = invent;
            
        }
        
        g2d.setPaint(new Color(0, 170, 0));
        
        for(int t=0; t<5; t++){
            
            for(int i=0; i<5; i++){
                
                g2d.fillOval(nodos[t][i].X(), nodos[t][i].Y(), Nodo.r, Nodo.r);
                
                g2d.setPaint(new Color(0, 100, 0));
                
                g2d.drawOval(nodos[t][i].X(), nodos[t][i].Y(), Nodo.r, Nodo.r);
                
                g2d.setPaint(Color.white);
                
                g.drawString((t+1)+","+i, nodos[t][i].X()+7, nodos[t][i].Y()+20);
                
                g2d.setPaint(new Color(0, 170, 0));
            }
            
        }
    }
    
}
